package GeneticPackage;
import java.util.ArrayList;
import java.util.List;

public class Population {
	private int Id_Population ;
	private int Population_Size ;
	private ArrayList<Chromosome> population ;

	public Population(int _Id_Population, int _Population_Size, List<Job> _jobs , List<Machine> _Machines){
		this.Id_Population = _Id_Population ;
		this.Population_Size = _Population_Size ;
		this.population = generate_random_population(_jobs , _Machines) ;

	}

	private ArrayList<Chromosome> generate_random_population(List<Job> _jobs , List<Machine> _Machines) {
		ArrayList<Chromosome> random_population = new ArrayList<Chromosome>();

		for(int i = 0; i<this.Population_Size ;i++) {
			random_population.add( new Chromosome(_jobs , _Machines) ) ;
		}

		return random_population;
	}

	public ArrayList<Chromosome> get_random_population(){
		return this.population ;
	}

	@Override
	public String toString() {
		return "Population[" + Id_Population + "] = "+ population;
	}

	public String toString_with_size() {
		return "Population[" + Id_Population + ", Population_Size=" + Population_Size + "], population="
				+ population + "]";
	}

}
