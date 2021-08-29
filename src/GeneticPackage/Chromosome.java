package GeneticPackage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;

public class Chromosome {

	private static int nembre_of_chromozome  ;
	private Integer Id_chromosome ;
	private LinkedHashMap<Job,Machine> chromosome ; // table 2 dim : Id_Job / Id_Machine
	private Integer fitness ;
	//private List Jobs ;
	//private List<Machine> Machines ;

	public Chromosome(List<Job> _jobs , List<Machine> _Machines ) {
		this.Id_chromosome = nembre_of_chromozome ;
		++nembre_of_chromozome;
		//		this.Jobs = _jobs ;
		//this.Machines = new ArrayList<Machine>(_Machines) ;
		this.chromosome = this.generate_Random_chromosome( _jobs, _Machines ) ;
		//this.chromosome = Arrays.copyOf(get_Random_chromosome(), get_Random_chromosome().length) ;
	}



	public Chromosome(LinkedHashMap<Job, Machine> chromosome) {
		this.Id_chromosome = nembre_of_chromozome ;
		++nembre_of_chromozome;
		//Id_chromosome = id_chromosome;
		this.chromosome = chromosome;
	}
	
	// for create the same chromozome with the same Id (like copy) 
	public Chromosome(Chromosome c) { 
		this.Id_chromosome = c.get_Id_chromosome() ;
		this.chromosome = new LinkedHashMap<>(c.get_chromosome());
		this.fitness = c.getFitness();
	}


	private LinkedHashMap<Job,Machine> generate_Random_chromosome(List<Job> _jobs, List<Machine> _Machines) {

		LinkedHashMap<Job,Machine> Random_chrom = new LinkedHashMap<Job,Machine>();
		Random rand = new Random();

		// we take a random job from this list at each step, until it is Empty
		List<Job> jobs = new ArrayList<Job>(_jobs) ;

		while( ! jobs.isEmpty() ) {

			// Take a random job from a list of jobs and remove it from the list
			int Indx_of_Id_Job_selected =  rand.nextInt(jobs.size()) ;
			Job job_selected = new Job(jobs.get(Indx_of_Id_Job_selected) ) ;
			jobs.remove(Indx_of_Id_Job_selected) ;	

			// Take a random machine among the valid machines of the selected job
			// ""rand.nextInt(job_selected.get_Id_Valid_Machines().size())"" give us the random index
			// but we take the value on the table ""job_selected.get_Id_Valid_Machines()"" at index found.
			int Indx_of_Id_Machine_selected = rand.nextInt(job_selected.get_Id_Valid_Machines().size()) ;
			Machine machine_selected_for_job_selected  = 
					new Machine(_Machines.get( job_selected.get_Id_Valid_Machines().get(Indx_of_Id_Machine_selected))) ;

			//Inject the job selected and his selected machine in the Map
			Random_chrom.put(job_selected, machine_selected_for_job_selected) ;

		}
		return Random_chrom;
	}

	public LinkedHashMap<Job,Machine> get_chromosome() {
		return this.chromosome ;
	}

	public Integer get_Id_chromosome() {
		return this.Id_chromosome ;
	}

	public Integer getFitness() {
		return fitness;
	}

	public void setFitness(Integer fitness) {
		this.fitness = fitness;
	}

	@Override
	public String toString() {
		return "\n Chromosome[" + Id_chromosome + "] = " + chromosome ;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Id_chromosome == null) ? 0 : Id_chromosome.hashCode());
		result = prime * result + ((chromosome == null) ? 0 : chromosome.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chromosome other = (Chromosome) obj;
		if (Id_chromosome == null) {
			if (other.Id_chromosome != null)
				return false;
		} else if (!Id_chromosome.equals(other.Id_chromosome))
			return false;
		if (chromosome == null) {
			if (other.chromosome != null)
				return false;
		} else if (!chromosome.equals(other.chromosome))
			return false;
		return true;
	}
}
