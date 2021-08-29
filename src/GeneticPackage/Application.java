package GeneticPackage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map.Entry;

public class Application {

	public static void main(String[] args) {
		
		// Data 
		double probability_mutation = 0.2 ;
		double probability_selection= 0.7;
		Integer limit_identical_best_generation = 150; //
		int _Nbr_Adjusters = 1 ;
		int _Nbr_Area = 2;
		int _Nbr_Limit_Machine_Area = 1;
		int _Nbr_Machine = 3 ;
		int _Nbr_Job = 6;
		int parameter_for_processing_time1 = 1 ;
		int parameter_for_processing_time2 = 9 ;
		double parameter_for_setup_time1 = 1.0 ;
		double parameter_for_setup_time2 = 1.0 ;

		DataForGenetic data = new DataForGenetic(_Nbr_Job, _Nbr_Machine, _Nbr_Adjusters ,
				_Nbr_Area, _Nbr_Limit_Machine_Area, parameter_for_processing_time1,
				parameter_for_processing_time2 , parameter_for_setup_time1, parameter_for_setup_time2) ;

		ArrayList<Job> jobs = new ArrayList<>();
		for(int i=0 ; i< data.getNumber_of_jobs(); i++) {
			jobs.add(new Job(i,data.getProcessTimes_On_Machines(i))) ;
			jobs.toString() ;
		}

		ArrayList<Machine> machines = new ArrayList<>();
		for(int m=0 ; m< data.getNumber_of_machines(); m++) {
			machines.add(new Machine(m, data.get_random_area_for_machine(m)) ) ;
		}

		int[][][] _Setup_Time_mij = data.getSetup_Time();
		for(int i=0;i<data.getNumber_of_machines();i++) {
			for(int j=0;j<data.getNumber_of_jobs();j++) {
				for(int k=0; k<data.getNumber_of_jobs();k++) {
					System.out.println( _Setup_Time_mij[i][j][k] );
				}
			}
		}



		//////////////////////////////////////////////////////////////////////////////////////////
		// genertate jobs and machines 
		//				Machine m0 = new Machine(0, 1) ;
		//		Machine m1 = new Machine(1, 0) ;
		//		Machine m2 = new Machine(2, 1) ;
		//
		//		ArrayList<ArrayList<Integer>> p0 = new ArrayList<>() ;
		//		ArrayList<ArrayList<Integer>> p1 = new ArrayList<>() ;
		//		ArrayList<ArrayList<Integer>> p2 = new ArrayList<>() ;
		//		ArrayList<ArrayList<Integer>> p3 = new ArrayList<>() ;
		//		ArrayList<ArrayList<Integer>> p4 = new ArrayList<>() ;
		//		ArrayList<ArrayList<Integer>> p5 = new ArrayList<>() ;
		//
		//		p0.add( new ArrayList<Integer>(Arrays.asList(0,18))) ;
		//		p0.add( new ArrayList<Integer>(Arrays.asList(1,12)));
		//		p0.add( new ArrayList<Integer>(Arrays.asList(2,15)));
		//
		//		p1.add( new ArrayList<Integer>(Arrays.asList(0,15)));
		//		p1.add( new ArrayList<Integer>(Arrays.asList(1,20))) ;
		//		p1.add( new ArrayList<Integer>(Arrays.asList(2,10))) ;
		//
		//		p2.add( new ArrayList<Integer>(Arrays.asList(0,10))) ;
		//		p2.add( new ArrayList<Integer>(Arrays.asList(1,18))) ;
		//		p2.add( new ArrayList<Integer>(Arrays.asList(2,14))) ;
		//
		//		p3.add( new ArrayList<Integer>(Arrays.asList(0,16))) ;
		//		p3.add( new ArrayList<Integer>(Arrays.asList(1,12))) ;
		//		p3.add( new ArrayList<Integer>(Arrays.asList(2,16))) ;
		//
		//		p4.add( new ArrayList<Integer>(Arrays.asList(0,14))) ;
		//		p4.add( new ArrayList<Integer>(Arrays.asList(1,10))) ;
		//		p4.add( new ArrayList<Integer>(Arrays.asList(2,12))) ;
		//
		//		p5.add( new ArrayList<Integer>(Arrays.asList(0,15))) ;
		//		p5.add( new ArrayList<Integer>(Arrays.asList(1,12))) ;
		//		p5.add( new ArrayList<Integer>(Arrays.asList(2,18))) ;
		//
		//		Job j0 = new Job(0, p0);
		//		Job j1 = new Job(1, p1);
		//		Job j2 = new Job(2, p2);
		//		Job j3 = new Job(3, p3);
		//		Job j4 = new Job(4, p4);
		//		Job j5 = new Job(5, p5);

		// create setup table

		//		int[][][] _Setup_Time_mij = { {  {0, 1, 2, 4, 3, 1 },{1, 0, 4, 4, 1, 4}, {2, 4, 0, 3, 3, 3},{3, 2, 4, 0, 1, 2}, { 4, 4, 3, 3, 0, 4} , {4, 1, 1, 2, 1, 0} },
		//				{  {0, 2, 3, 3, 4, 4 },{2, 0, 1, 1, 4, 2}, { 4, 3, 0, 4, 3, 4},{ 4, 4, 2, 0, 1, 1}, {1, 2, 4, 3, 0, 4},  {2, 3, 2, 3, 2, 0}  },
		//				{  {0, 2, 5, 1, 1, 3 },{4, 0, 3, 1, 1, 1}, {0, 2, 5, 1, 1, 3},{4, 0, 3, 1, 1, 1}, {1, 1, 1, 2, 0, 1},  {1, 2, 2, 1, 3, 0}  },
		//		};


		//		ArrayList<Job> jobs = new ArrayList<>(Arrays.asList(j0,j1,j2,j3,j4,j5));
		//		ArrayList<Machine> machines = new ArrayList<>(Arrays.asList(m0,m1,m2)) ;

		Heuristic heuristique = new Heuristic(probability_mutation, probability_selection, limit_identical_best_generation, data);
		Population population0 = new Population(0, 100, jobs, machines) ;

		heuristique.run(
				heuristique.select_population(heuristique.evaluate_population(population0.get_random_population())) );
		System.out.println("Best solution found : "+ heuristique.getBest_known_solution().toString()+"\n" ) ;

		for(Entry<Job,Machine> p: heuristique.getBest_known_solution().get_chromosome().entrySet() )
			System.out.println("job "+p.getKey()+" start at "+ p.getKey().getStart_Job()+" on "
					+p.getValue().toString()+" during "
					+p.getKey().get_ProcessTime_On_Machine(p.getValue().get_Id_Machine())+" (unit)");

		System.out.println( "\nC_max = "+heuristique.getBest_known_solution().getFitness()+"\n");















	}

}
