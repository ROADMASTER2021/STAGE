package GeneticPackage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class DataForGenetic {
	private int number_of_jobs ;
	private int number_of_machines ;
	private int[][][] Setup_Time ;
	private int number_of_adjusters ;
	private int number_of_areas ;
	private int number__limit_Machine_Area ;
	private ArrayList<ArrayList<ArrayList<Integer>>> ProcessTimes_On_Machines ;
	private ArrayList<Integer> Area_for_machines ;


	public DataForGenetic(int number_of_jobs, int number_of_machines,
			int number_of_adjusters,int number_of_areas,int number__limit_Machine_Area,
			int parameter_A_jobs,int parameter_B_jobs,double parameter_A_machines,double parameter_B_machines) {
		
		this.number_of_jobs = number_of_jobs ;
		this.number_of_machines = number_of_machines ;
		this.number_of_areas = number_of_areas ;
		this.number__limit_Machine_Area = number__limit_Machine_Area ;
		this.number_of_adjusters = number_of_adjusters ;
		this.Area_for_machines = generate_random_area_for_machine();
		this.ProcessTimes_On_Machines = generate_ProcessTimes_On_Machines(parameter_A_jobs, parameter_B_jobs) ;
		this.Setup_Time = generate_setup_time(parameter_A_machines, parameter_B_machines) ;
	}

	// we generate processTime of jobs into interval 
	private ArrayList<ArrayList<ArrayList<Integer>>> generate_ProcessTimes_On_Machines(int parameter_A_jobs,int parameter_B_jobs) {
		ArrayList<ArrayList<ArrayList<Integer>>> process = new ArrayList<>();
		Random rand = new Random();

		for(int i=0; i<this.number_of_jobs; i++) {
			ArrayList<ArrayList<Integer>> tmp = new ArrayList<>();
			for(int m=0; m<this.number_of_machines; m++) {
				int RandomProcessTime = parameter_A_jobs + rand.nextInt(parameter_B_jobs - parameter_A_jobs + 1) ;
				ArrayList<Integer> NewProcess = new ArrayList<>(Arrays.asList(m,RandomProcessTime )) ;
				tmp.add(NewProcess) ;
			}
			process.add(tmp) ;
		}
		
		return process;
	}

	// we genertae Setup time between jobs
	private int[][][] generate_setup_time(double parameter_A_machines,double parameter_B_machines){
		int[][][] setup_time_mij = new int[this.number_of_machines][this.number_of_jobs][this.number_of_jobs];
		Random rand = new Random();
		double a =   rand.nextDouble()*(parameter_B_machines - parameter_A_machines) + parameter_A_machines ;

		for(int m=0; m<this.number_of_machines; m++) {
			for(int i=0; i<this.number_of_jobs; i++) {
				for(int j=0; j< this.number_of_jobs; j++) {
					setup_time_mij[m][i][j] = (int) (a * Math.min(this.ProcessTimes_On_Machines.get(i).get(m).get(1),
							this.ProcessTimes_On_Machines.get(j).get(m).get(1) ));
				}	
			}
		}
		return setup_time_mij ;
	}


	// we generate random area for machines 
	private ArrayList<Integer> generate_random_area_for_machine() {
		ArrayList<Integer> random_Areas = new ArrayList<>();
		Random r = new Random();

		for(int m=0; m < this.number_of_machines; m++)
			random_Areas.add(r.nextInt(this.number_of_areas)) ;

		return random_Areas;
	}


	public int get_random_area_for_machine(int id_machine) {
		return this.Area_for_machines.get(id_machine) ;
	}


	public ArrayList<ArrayList<Integer>> getProcessTimes_On_Machines(int id_job) {
		return this.ProcessTimes_On_Machines.get(id_job);
	}


	public int getNumber_of_jobs() {
		return number_of_jobs;
	}


	public int getNumber_of_machines() {
		return number_of_machines;
	}


	public int[][][] getSetup_Time() {
		return this.Setup_Time;
	}


	public int getNumber_of_adjusters() {
		return number_of_adjusters;
	}


	public int getNumber_of_areas() {
		return number_of_areas;
	}


	public int getNumber__limit_Machine_Area() {
		return number__limit_Machine_Area;
	}



}
