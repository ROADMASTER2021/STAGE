package GeneticPackage;
import java.util.Comparator;

public class Compare_fitness implements Comparator<Chromosome>{

	@Override
	public int compare(Chromosome o1, Chromosome o2) {
		
		if(o1.getFitness() > o2.getFitness())
			return -1;

		if(o1.getFitness() == o2.getFitness())
			return 0;

		return 1;
	}

}

