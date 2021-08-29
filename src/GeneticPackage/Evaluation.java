package GeneticPackage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map.Entry;

public class Evaluation {
	private int[][][] Setup_Time_mij ;
	private int Nbr_Adjusters ;
	private int Nbr_Area ;
	private int Nbr_Limit_Machine_Area ; 
	private int Nbr_Machine ; // voir si on peut la supprimer 
	private int Nbr_Job;


	public Evaluation(DataForGenetic data) {
		this.Setup_Time_mij = data.getSetup_Time() ;
		this.Nbr_Adjusters = data.getNumber_of_adjusters() ;
		this.Nbr_Area = data.getNumber_of_areas() ;
		this.Nbr_Limit_Machine_Area = data.getNumber__limit_Machine_Area() ; // we consider same limit for all areas 
		this.Nbr_Machine = data.getNumber_of_machines();
		this.Nbr_Job = data.getNumber_of_jobs() ;
	}

	public void Evaluate_chromosome(Chromosome _chromosome) {
		/*
		 * the tables (1),(2) and (3) initialized to '0' by default 
		 * (1) the time of the end of the last job on the machines (1 and 2: go together)
		 * (2) table for register the last job in each machine
		 * (3) the time of the first availability of each adjuster
		 * (4)
		 */

		int[] Machines_availability_time = new int[this.Nbr_Machine]  ;  // (1)
		int[] Id_Last_Job_On_machines = new int[this.Nbr_Machine] ;// (2)	

		ArrayList<Integer> Adjusters_availability_time = new ArrayList<Integer>() ;
		for(int i=0; i<this.Nbr_Adjusters; i++ ) {
			Adjusters_availability_time.add(0) ;
		}

		ArrayList<ArrayList<Integer>> Area_time_machines = new ArrayList<>() ; 
		for(int i=0; i<this.Nbr_Area; i++ ) {
			Area_time_machines.add(new ArrayList<Integer>()) ;
		}
		int[] C = new int[this.Nbr_Job] ; // jobs finishing time, // (3)

		int start_job = 0, setup_start = 0 , setup_finish = 0; 
		int indx_Min_time_End_Machine_On_Area, indx_min_Time_adjuster_used ; 

		for(Entry<Job, Machine> pair: _chromosome.get_chromosome().entrySet()) {

			/* (1) we see if this is the first job on the machine (if t=0 on this machine)
			 * because in this case we don't need Adjuster ( S_m0j == 0)
			 */

			if( Machines_availability_time[ pair.getValue().get_Id_Machine() ] == 0 ) {  // (1) 

				//we see if the area that contains this machine, has another machines working in progress 
				if( Area_time_machines.get(pair.getValue().get_Id_Area()).size() < this.Nbr_Limit_Machine_Area ) {
					C[ pair.getKey().get_Id() ] =  pair.getKey().get_ProcessTime_On_Machine(pair.getValue().get_Id_Machine()) ; // C_j = p_mj
					pair.getKey().setStart_Job(0) ;

					// we inject the end time of this machine in Area_time_machines
					Area_time_machines.get(pair.getValue().get_Id_Area()).add(  C[pair.getKey().get_Id()]  ) ; 

					// we update availability of machine which fabricate the job
					Machines_availability_time[ pair.getValue().get_Id_Machine() ] = C[ pair.getKey().get_Id() ] ; 

					// we update the last job on the machine 
					Id_Last_Job_On_machines[ pair.getValue().get_Id_Machine() ] = pair.getKey().get_Id() ;



				}
				else { // we have reached the limit of the surface (if Nbr >= limit) / and we are at the start of machine

					// we have 5 machines (limit) work at the same time, so the new machine must start after the min finish_machine (start_job)  
					start_job = Collections.min( Area_time_machines.get( pair.getValue().get_Id_Area() ) ) ; 


					// we keep the index of the min found and we remove it
					indx_Min_time_End_Machine_On_Area = Area_time_machines.get( pair.getValue().get_Id_Area() ).indexOf( start_job ) ;					
					Area_time_machines.get( pair.getValue().get_Id_Area() ).remove(indx_Min_time_End_Machine_On_Area) ;

					// we update C_j with : start_job (min of list) + p_mj
					C[ pair.getKey().get_Id() ] = start_job + pair.getKey().get_ProcessTime_On_Machine(pair.getValue().get_Id_Machine()); 
					pair.getKey().setStart_Job(start_job) ;

					// we add the new 'time_end_machine' == C_j in the corresponding area (~ we replace the min)
					Area_time_machines.get( pair.getValue().get_Id_Area() ).add( C[ pair.getKey().get_Id() ] ) ;

					// we update availability of machine which fabricate the job
					Machines_availability_time[ pair.getValue().get_Id_Machine() ] = C[ pair.getKey().get_Id() ] ; 

					// we update the last job on the machine  
					Id_Last_Job_On_machines[ pair.getValue().get_Id_Machine() ] = pair.getKey().get_Id() ;
				}
			}		
			else { // the machine has been used (ie: availability of machine is not at t = 0 )

				//calculate setup starting time == MAX( first availability of adjuster, time of availability of machine )
				setup_start = Math.max( Collections.min(Adjusters_availability_time) , 
						Machines_availability_time[pair.getValue().get_Id_Machine() ]) ;

				//keep the index of adjuster time used for this setup
				indx_min_Time_adjuster_used =  Adjusters_availability_time.indexOf( Collections.min(Adjusters_availability_time) ) ;

				//calculate setup finishing time == setup starting time + s_mij
				setup_finish = setup_start + Setup_Time_mij[ pair.getValue().get_Id_Machine() ]
						[ Id_Last_Job_On_machines[pair.getValue().get_Id_Machine()] ]
								[ pair.getKey().get_Id() ];

				//remove the adjuster time used and Update the time of availability of adjuster (replace by: setup_finish time)
				Adjusters_availability_time.remove(indx_min_Time_adjuster_used) ;
				Adjusters_availability_time.add( setup_finish ) ;

				if( Area_time_machines.get(pair.getValue().get_Id_Area()).size() < this.Nbr_Limit_Machine_Area ) {

					// calculate job starting time == setup_finish (don't care about the area )
					start_job = setup_finish;

					// calculate job finishing time C[j] == starting job time + p_mj || and register the succesoor of the job 
					C[ pair.getKey().get_Id() ] =  start_job + pair.getKey().get_ProcessTime_On_Machine( pair.getValue().get_Id_Machine() ); 
					pair.getKey().setStart_Job(start_job) ;
					pair.getKey().setId_predecessor( Id_Last_Job_On_machines[pair.getValue().get_Id_Machine() ] ) ;
					

					// Registration of the job j as the last job on the machine m
					Id_Last_Job_On_machines[ pair.getValue().get_Id_Machine() ] = pair.getKey().get_Id() ;

					// Update the time of availability of resources (machine m, area)
					Machines_availability_time[ pair.getValue().get_Id_Machine() ] = C[ pair.getKey().get_Id() ] ;
					Area_time_machines.get( pair.getValue().get_Id_Area() ).add( C[ pair.getKey().get_Id() ] ) ; 
				}
				else { // Nbr of machines work at the same time in the aera == authorized limit 

					// calculate job starting time == Max(setup_finish ,time of availability of machine, first availability of area (the min of machines includ in the area))
					start_job = Math.max( setup_finish,  Collections.min( Area_time_machines.get(pair.getValue().get_Id_Area())) );

					//keep the index of the min time machine found on this area
					indx_Min_time_End_Machine_On_Area = Area_time_machines.get(pair.getValue().get_Id_Area()).
							indexOf( Collections.min( Area_time_machines.get(pair.getValue().get_Id_Area())) );

					// calculate job finishing time C[j] == starting job time + p_mj || and register the succesoor of the job j 
					C[ pair.getKey().get_Id() ] =  start_job + pair.getKey().get_ProcessTime_On_Machine( pair.getValue().get_Id_Machine() ); 
					pair.getKey().setStart_Job(start_job) ;
					pair.getKey().setId_predecessor( Id_Last_Job_On_machines[pair.getValue().get_Id_Machine() ] ) ;

					// Registration of the job j as the last job on the machine m
					Id_Last_Job_On_machines[ pair.getValue().get_Id_Machine() ] = pair.getKey().get_Id() ;

					// Update the time of availability of resources (machine m, area)
					Machines_availability_time[ pair.getValue().get_Id_Machine() ] = C[ pair.getKey().get_Id() ] ;
					Area_time_machines.get(pair.getValue().get_Id_Area()).remove(indx_Min_time_End_Machine_On_Area) ;
					Area_time_machines.get(pair.getValue().get_Id_Area()).add( C[ pair.getKey().get_Id() ] ) ;
				}
			}
		}
		// return the max of the table C_i ( C_max )
		_chromosome.setFitness( Arrays.stream(C).max().getAsInt() ) ;

	}
}
