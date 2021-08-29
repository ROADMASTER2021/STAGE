package GeneticPackage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;


public class Heuristic {


	private double probability_mutation  = 0.2 ;
	private double proportion_selection  = 0.7 ;
	private Integer limit_identical_best_generation;
	private Evaluation evaluation;
	private Operators operator ;
	private Compare_fitness compare_decreasing_fitness ;
	private Compare_fitness_inverse compare_incresing_fitness ;
	private Chromosome Best_solution_of_the_generation ;
	private Chromosome Best_known_solution ;


	public Heuristic(double probability_mutation, double probability_selection, Integer limit_identical_best_generation, DataForGenetic data) {
		this.probability_mutation = probability_mutation;
		this.proportion_selection = probability_selection;
		this.limit_identical_best_generation = limit_identical_best_generation;
		this.evaluation = new Evaluation(data);
		this.compare_decreasing_fitness = new Compare_fitness();
		this.compare_incresing_fitness = new Compare_fitness_inverse();
		this.operator = new Operators(data.getNumber_of_areas());
	}

	// 1) we initialize the population ( with class population )

	//=========================================================================================================//

	// 2) We evaluate the population generated
	public 	ArrayList<Chromosome> evaluate_population(ArrayList<Chromosome> population){
		// evaluate population
		for(Chromosome c: population) {
			this.evaluation.Evaluate_chromosome(c);
		}

		// return population evaluated
		return population;
	}
	//=========================================================================================================//

	// 3) We Sort and Select the population evaluated 
	public ArrayList<Chromosome> select_population(ArrayList<Chromosome> population){

		// Sort individuals according to their fitness
		Collections.sort(population, this.compare_decreasing_fitness); // must be see the implementation of Compare_fitnee

		// calculate the sum of all fitness (the denominator)
		int sum_fitness = 0;
		for(Chromosome c: population)
			sum_fitness += c.getFitness();

		// we apply the "wheel roulette" method and we return a sorted list of (Chromosome)
		double sum_iterate = 0 ;
		ArrayList<Chromosome> population_select_Wheel_list = new ArrayList<>() ;
		for(Chromosome c: population) {
			sum_iterate += c.getFitness() ;
			if( (double)(sum_iterate / sum_fitness) >= this.proportion_selection )
				population_select_Wheel_list.add(c);
		}

		//return the individuals selected 
		return population_select_Wheel_list ; 
	}
	//=========================================================================================================//

	public void run(ArrayList<Chromosome>  population_select_Wheel_list ) {
		int increment_identical_best = 0;
		Collections.sort(population_select_Wheel_list, this.compare_incresing_fitness); // c_max_0 < c_max_1< ....

		// we keep a copie of the best know solution and the best solution of generation with a last element of this arrayList
		this.Best_known_solution = new Chromosome (population_select_Wheel_list.get(0) ); 
		this.Best_solution_of_the_generation = new Chromosome (population_select_Wheel_list.get(0) );

		ArrayList<Chromosome> New_generation = new ArrayList<>();
		Random random_proba_mutation = new Random();
		Random random_proba_crossover = new Random();

		// START OF ALGORITHM
		while (increment_identical_best < this.limit_identical_best_generation) {

			//we create new generation
			for(int i=0 ; i<population_select_Wheel_list.size()-1 ; i++) {
				for(int j=i+1 ; j<population_select_Wheel_list.size() ; j++) {

					//generate chromozome with 2 parents selected, with 6 operators 
					Chromosome offspring_generated =
							this.operator.get_offspring_By_Crossover(population_select_Wheel_list.get(i),
									population_select_Wheel_list.get(j), random_proba_crossover.nextInt(6)); 

					// apply the mutation if random probabibly > probability_mutation
					if( random_proba_mutation.nextDouble() > this.probability_mutation ) {
						this.operator.get_offspring_By_Mutation(offspring_generated,0) ;
					}

					// evaluate the new chromozome
					this.evaluation.Evaluate_chromosome(offspring_generated);

					// check if it is better than the best one on the new generation
					if( offspring_generated.getFitness() < this.Best_solution_of_the_generation.getFitness()) {
						this.Best_solution_of_the_generation = new Chromosome(offspring_generated) ;
					}

					// inject the new chromozome on the new generation
					New_generation.add(offspring_generated) ;

					// we stop if new generation has doubled
					if(New_generation.size() == 2*population_select_Wheel_list.size())
						break ;
				}
				// we stop if new generation has doubled
				if(New_generation.size() == 2*population_select_Wheel_list.size())
					break ;
			}

			// we select the new generation and we repeat the algorithm with her
			population_select_Wheel_list.clear();
			population_select_Wheel_list.addAll( this.select_population(New_generation) );

			// we update the BEST KNOW solution, 
			// otherwise it means that it is the same solution that is repeated => INCREMENT the 'increment_identical_best'
			if( this.Best_solution_of_the_generation.getFitness() < this.Best_known_solution.getFitness()  ) {
				this.Best_known_solution = new Chromosome( this.Best_solution_of_the_generation ) ;
			}else {
				increment_identical_best++ ;
			}

			//Empty the list of new generation for re-use it
			New_generation.clear();

		}
		// END OF ALGORITHM

	}


	public Chromosome getBest_known_solution() {
		return Best_known_solution;
	}
}




