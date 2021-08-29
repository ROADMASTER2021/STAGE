package Cplex;

import GeneticPackage.DataForGenetic;
public class DataForModel extends DataForGenetic{

	private int[][][] Setup_Time_model ;
	private int[][] ProcessTimes_On_Machines_model ;
	private int[] Area_limit_machines_model ; // nbre limit per area
	private int[][] if_machine_on_area_model ;
	private int[][] if_job_valid_for_machine_model ;
	private int Horizon_for_model ;


	public DataForModel(int number_of_jobs, int number_of_machines, int number_of_adjusters, int number_of_areas,
			int number__limit_Machine_Area, int parameter_A_jobs, int parameter_B_jobs, double parameter_A_machines,
			double parameter_B_machines) {
		super(number_of_jobs, number_of_machines, number_of_adjusters, number_of_areas, number__limit_Machine_Area,
				parameter_A_jobs, parameter_B_jobs, parameter_A_machines, parameter_B_machines);
		this.Setup_Time_model = generate_setup_time_for_model();
		this.Area_limit_machines_model = fill_in_table_limit_machine_per_area_model();
		this.if_machine_on_area_model = fill_in_area_machine_model();
		this.ProcessTimes_On_Machines_model = generate_processTime_model();
		this.if_job_valid_for_machine_model = fill_in_machine_job_valid();
		this.Horizon_for_model = calculate_horizon();

	}


	// e[m][i]
	private int[][] fill_in_machine_job_valid() {
		int[][] tmp = new int[super.getNumber_of_machines()][super.getNumber_of_jobs()] ;

		for(int m=0; m<super.getNumber_of_machines() ; m++) {
			for(int i=0 ; i<super.getNumber_of_jobs() ; i++) {
				tmp[m][i] = 1 ;	
			}
		}
		return tmp;
	}

	// process time for model   p[m][i]
	private int[][] generate_processTime_model() {
		int[][] processTime = new int[super.getNumber_of_machines()][super.getNumber_of_jobs()+1] ;
		for(int i=0 ; i<super.getNumber_of_jobs() ; i++) {
			for(int m=0; m<super.getNumber_of_machines() ; m++) {
				if(i==0) {
					processTime[m][i] = 0 ;
				}else {
					processTime[m][i] = super.getProcessTimes_On_Machines(i).get(m).get(1) ;	
				}
			}
		}
		return processTime;
	}


	// area which contains machine   z[m][w]
	private int[][] fill_in_area_machine_model() {

		int[][] tmp = new int[super.getNumber_of_machines()][super.getNumber_of_areas()] ;

		for(int m=0; m<super.getNumber_of_machines() ; m++) {
			for(int ar = 0 ; ar < super.getNumber_of_areas(); ar++) {
				if( super.get_random_area_for_machine(m) == ar ) {
					tmp[m][ar] = 1 ;
				}
			}
		}

		return tmp;
	}


	// table of number limit machines for each area  U[w]
	private int[] fill_in_table_limit_machine_per_area_model() {
		int[] tmp = new int[super.getNumber_of_areas()] ;
		for(int i=0; i<super.getNumber_of_areas(); i++) {
			tmp[i] = super.getNumber__limit_Machine_Area() ;
		}
		return tmp;
	}


	// setup time table for model (includes job '0')  s[m][i][j]
	private int[][][] generate_setup_time_for_model() {

		int[][][] stp = new int[super.getNumber_of_machines()][super.getNumber_of_jobs()+1][super.getNumber_of_jobs()+1] ;
		for(int m=0; m<super.getNumber_of_machines();m++) {
			for(int i=1; i<super.getNumber_of_jobs()+1 ; i++) {
				for(int j=0 ; j<super.getNumber_of_jobs()+1 ; j++) {
					if(j==0) {
						stp[m][i][j] = Integer.MAX_VALUE ;
					}else {
						stp[m][i][j] = super.getSetup_Time()[m][i-1][j-1] ;
					}
				}
			}
		}

		return stp;
	}


	private int calculate_horizon() {

		// 1st part of H
		int[] max_m = new int[super.getNumber_of_jobs()]  ;
		int max_w = 0 ;
		int sum_max_j = 0;

		for(int ww = 0; ww<super.getNumber_of_areas() ;ww++) {
			for(int jj=0 ;jj< super.getNumber_of_jobs() ; jj++) {
				for(int mm=0 ; mm< super.getNumber_of_machines(); mm++) {
					if(max_m[jj] < Math.multiplyExact( Math.multiplyExact(this.ProcessTimes_On_Machines_model[mm][jj+1], this.if_job_valid_for_machine_model[mm][jj])  , this.if_machine_on_area_model[mm][ww]) )
						max_m[jj] = Math.multiplyExact( Math.multiplyExact(ProcessTimes_On_Machines_model[mm][jj+1], this.if_job_valid_for_machine_model[mm][jj])  , this.if_machine_on_area_model[mm][ww]) ; 
				}
				sum_max_j += max_m[jj] ;
			}
			if( max_w < sum_max_j )
				max_w = sum_max_j ;
		}

		// 2nd part of H 
		int max_mmm= 0 ;
		int sum_jjj = 0 ;
		int max_iii = 0;

		for(int jjj =1 ; jjj< super.getNumber_of_jobs()+1 ; jjj++) {
			for(int mmm=0; mmm<super.getNumber_of_machines() ;mmm++) {
				for(int iii=1 ; iii< super.getNumber_of_jobs()+1; iii++) {
					if( max_iii < Math.multiplyExact(this.Setup_Time_model[mmm][iii][jjj], this.if_job_valid_for_machine_model[mmm][jjj-1]) )
						max_iii = Math.multiplyExact(this.Setup_Time_model[mmm][iii][jjj], this.if_job_valid_for_machine_model[mmm][jjj-1]) ;
				}
				if(max_mmm < max_iii)
					max_mmm = max_iii ;
			}
			sum_jjj += max_mmm ;
		}
		
		return max_w + sum_jjj ;
	}

	public int getHorizon_for_model() {
		return Horizon_for_model;
	}


	public int[][][] getSetup_Time_model() {
		return Setup_Time_model;
	}

	public int[][] getProcessTimes_On_Machines_model() {
		return ProcessTimes_On_Machines_model;
	}

	public int[] getArea_limit_machines_model() {
		return Area_limit_machines_model;
	}

	public int[][] getIf_machine_on_area_model() {
		return if_machine_on_area_model;
	}

	public int[][] getIf_job_valid_for_machine_model() {
		return if_job_valid_for_machine_model;
	}







}
