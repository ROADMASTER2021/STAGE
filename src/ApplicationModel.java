import java.util.ArrayList;
import java.util.Map.Entry;

import Cplex.DataForModel;
import Cplex.ModelCplex;
import GeneticPackage.Heuristic;
import GeneticPackage.Job;
import GeneticPackage.Machine;
import GeneticPackage.Population;

public class ApplicationModel {

	public static void main(String[] args) throws InterruptedException {
		double probability_mutation = 0.2 ;
		double probability_selection= 0.7;
		Integer limit_identical_best_generation = 150; 

		int _Nbr_Adjusters = 1 ;
		int _Nbr_Area = 2;
		int _Nbr_Limit_Machine_Area = 1;
		int _Nbr_Machine = 3 ;
		int _Nbr_Job = 8;

		int parameter_for_processing_time1 = 1 ;
		int parameter_for_processing_time2 = 9 ;
		double parameter_for_setup_time1 = 0.2 ;
		double parameter_for_setup_time2 = 0.5 ;

		DataForModel data = new DataForModel(_Nbr_Job, _Nbr_Machine, _Nbr_Adjusters ,
				_Nbr_Area, _Nbr_Limit_Machine_Area, parameter_for_processing_time1,
				parameter_for_processing_time2 , parameter_for_setup_time1, parameter_for_setup_time2) ;

		////////////////////////////// MODEL //////////////////////////////////////////////////////////////


		ModelCplex.solveModel(data);

		//////////////////////////////  HEURISTIC  //////////////////////////////////////////////////////////////


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
				}
			}
		}

		Heuristic heuristique = new Heuristic(probability_mutation, probability_selection, limit_identical_best_generation, data);
		Population population0 = new Population(0, 100, jobs, machines) ;


		// Run Genetic algorithm
		long startTime = System.currentTimeMillis();

		int i=0 ;
		int best_cmax = data.getHorizon_for_model();
		while (i< 15) {
			heuristique.run(
					heuristique.select_population(heuristique.evaluate_population(population0.get_random_population())) );
			System.out.println("Best solution found : "+ heuristique.getBest_known_solution().toString()+"\n" ) ;

			if(best_cmax > heuristique.getBest_known_solution().getFitness() )
				best_cmax = heuristique.getBest_known_solution().getFitness();
			
			i++;
		}
		long endTime = System.currentTimeMillis();



		for(Entry<Job,Machine> p: heuristique.getBest_known_solution().get_chromosome().entrySet() )
			System.out.println("job "+p.getKey()+" start at "+ p.getKey().getStart_Job()+" on "
					+p.getValue().toString()+" during "
					+p.getKey().get_ProcessTime_On_Machine(p.getValue().get_Id_Machine())+" (unit)");

		System.out.println( "\nC_max = "+heuristique.getBest_known_solution().getFitness()+"\n");

		System.out.println("That took " + (endTime - startTime) + " milliseconds");
		
		System.out.println("best heuritic solution = "+ best_cmax);



	}






}


