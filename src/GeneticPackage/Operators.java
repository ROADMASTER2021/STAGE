package GeneticPackage;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Random;

public class Operators {

	private int Nbr_Area;

	public Operators(int _Nbr_Area) {
		this.Nbr_Area = _Nbr_Area ;
	}

	//================================= CROSSOVERS ============================================================//

	/* 
	 *  int type :  type of crossover 
	 *  type 0 ==> One Point Order Crossover
	 *  type 1 ==> Machine assignment Crossover
	 *  type 2 ==> Two Points Order Crossover
	 *  type 3 ==> Random Selection Crossover
	 *  type 4 ==> Best Area Gathering Crossover
	 *  type 5 ==> Best Area Conservation Crossover
	 */
	public  Chromosome get_offspring_By_Crossover(Chromosome parent_1, Chromosome parent_2, int type) {

		LinkedHashMap<Job, Machine> offspring_map = new LinkedHashMap<>();

		switch (type) {

		case 0:
			Random rand = new Random();
			int random_point_crossover = rand.nextInt( parent_1.get_chromosome().size() );
			Iterator<Entry<Job, Machine>> it_parent_1 = parent_1.get_chromosome().entrySet().iterator();

			int iter=0 ;
			//iterate parent 1 and transfer the 'rand' first pair into the offspring
			while ( it_parent_1.hasNext() && iter < random_point_crossover ) {
				iter++ ;
				Entry<Job,Machine> pair1 = it_parent_1.next() ;
				offspring_map.put( pair1.getKey() , pair1.getValue() ) ; ;
			}
			// iterate parent 2 and transfer the others 'rand' which are not in the offspring
			Iterator<Entry<Job, Machine>> it_parent_2 = parent_2.get_chromosome().entrySet().iterator();
			while(it_parent_2.hasNext()) {
				Entry<Job,Machine> pair2 = it_parent_2.next() ;
				// we check that the offspring don't contains already this key
				if( ! offspring_map.containsKey(pair2.getKey()) ) {
					offspring_map.put( pair2.getKey() , pair2.getValue() ) ;
				}
			}
			break;

		case 1:
			Iterator<Entry<Job,Machine>> itt_parent_1 = parent_1.get_chromosome().entrySet().iterator();
			while(itt_parent_1.hasNext()) {
				Entry<Job,Machine> pair3 = itt_parent_1.next();
				offspring_map.put(pair3.getKey(), parent_2.get_chromosome().get(pair3.getKey()) ) ;
			}
			break;

		case 2:
			Random rdm = new Random();
			int random_point_1_crossover, random_point_2_crossover ;
			do {
				random_point_1_crossover = rdm.nextInt( parent_1.get_chromosome().size()/2  );
				random_point_2_crossover = rdm.nextInt( parent_1.get_chromosome().size()/2 );
			}
			while(random_point_1_crossover == 0 || random_point_2_crossover == 0);

			// we clone the parent 1 on the offspring
			offspring_map.putAll( parent_1.get_chromosome() );

			//we take the iterator until position =rand1, then we replace the values (not key) from the values of parent 2
			int i=0 ;
			Iterator<Entry<Job, Machine>> iter_parent_1 = parent_1.get_chromosome().entrySet().iterator();
			while(iter_parent_1.hasNext() && i<random_point_1_crossover) {
				iter_parent_1.next();
				i++ ;
			}

			/*after cloning the parent 1, we replace elements between 
			(rand1) and (size - rand2) with the missing elements from parent 2 */
			while(iter_parent_1.hasNext() && i < parent_1.get_chromosome().size() - random_point_2_crossover) {
				Entry<Job,Machine> pair4 = iter_parent_1.next();
				offspring_map.replace(pair4.getKey(), parent_2.get_chromosome().get(pair4.getKey())) ;
				i++;
			}
			break;

		case 3:
			double[] proba = new double[parent_1.get_chromosome().size()];
			Random proba_crossover = new Random();
			LinkedHashMap<Job, Machine> parent1_register = new LinkedHashMap<>();
			LinkedHashMap<Job, Machine> parent2_register = new LinkedHashMap<>();

			for(double d: proba)
				d = proba_crossover.nextDouble();

			int d_iterate = 0 ;
			for(Entry<Job,Machine> jm1: parent_1.get_chromosome().entrySet()) {
				if( proba[d_iterate] > 0.4 ) {
					parent1_register.put(jm1.getKey(), jm1.getValue()) ;

					d_iterate++ ;
				}
			}
			for(Entry<Job,Machine> jm2: parent_2.get_chromosome().entrySet()) {
				if( ! parent1_register.containsKey(jm2.getKey()) )
					parent2_register.put(jm2.getKey(), jm2.getValue()) ;
			}
			Iterator<Entry<Job, Machine>> i1 = parent_1.get_chromosome().entrySet().iterator();
			Iterator<Entry<Job, Machine>> ir1 = parent1_register.entrySet().iterator();
			Iterator<Entry<Job, Machine>> ir2 = parent2_register.entrySet().iterator();

			if(parent1_register.isEmpty()) {
				offspring_map.putAll(parent_2.get_chromosome());
			}else {
				if(parent2_register.isEmpty()) {
					offspring_map.putAll(parent_1.get_chromosome());
				}


				Entry<Job,Machine> pr1 = ir1.next();
				Entry<Job,Machine> pr2 = ir2.next();
				while(i1.hasNext()) {
					Entry<Job,Machine> p1 = i1.next();

					if(p1.getKey().equals(pr1.getKey())) {
						offspring_map.put(p1.getKey(), p1.getValue()) ;
						pr1 = ir1.next();
					}else {
						offspring_map.put(pr2.getKey(), pr2.getValue()) ;
						pr2 = ir2.next(); 
					}

				}
			}
			break;

		case 4:
			int[] cmax_area = new int[this.Nbr_Area] ;
			int minim_area = 0 ;
			int AreaindexOfMin = 0 ;

			for(Entry<Job,Machine> area_cross: parent_1.get_chromosome().entrySet() ) {
				if( (area_cross.getKey().getStart_Job() + area_cross.getKey().get_ProcessTime_On_Machine(area_cross.getValue().get_Id_Machine())) > cmax_area[area_cross.getValue().get_Id_Area()]  )
				{
					cmax_area[area_cross.getValue().get_Id_Area()] =  (area_cross.getKey().getStart_Job() + area_cross.getKey().get_ProcessTime_On_Machine(area_cross.getValue().get_Id_Machine())) ;
					if(minim_area < cmax_area[area_cross.getValue().get_Id_Area()] ) {
						//minim_area = cmax_area[area_cross.getValue().get_Id_Area()] ;
						AreaindexOfMin = area_cross.getValue().get_Id_Area() ;
					}
				}
			}
			// regroup all jobs of best area cmax on the begining of offspring
			for(Entry<Job,Machine> area_pair1: parent_1.get_chromosome().entrySet()) {
				if(area_pair1.getValue().get_Id_Area() == AreaindexOfMin)
					offspring_map.put(area_pair1.getKey(), area_pair1.getValue()) ;
			}

			for(Entry<Job,Machine> area_pair2: parent_2.get_chromosome().entrySet() ) {
				if(! offspring_map.containsKey(area_pair2.getKey()))
					offspring_map.put(area_pair2.getKey(),area_pair2.getValue()) ;

			}
			break;

		case 5:
			int[] cmax_area2 = new int[this.Nbr_Area] ;
			int minim_area2 = 0 ;
			int AreaindexOfMin2 = 0 ;

			for(Entry<Job,Machine> area_cross1: parent_1.get_chromosome().entrySet() ) {
				if( (area_cross1.getKey().getStart_Job() + area_cross1.getKey().get_ProcessTime_On_Machine(area_cross1.getValue().get_Id_Machine())) > cmax_area2[area_cross1.getValue().get_Id_Area()]  )
				{
					cmax_area2[area_cross1.getValue().get_Id_Area()] =  (area_cross1.getKey().getStart_Job() + area_cross1.getKey().get_ProcessTime_On_Machine(area_cross1.getValue().get_Id_Machine())) ;
					if(minim_area2 < cmax_area2[area_cross1.getValue().get_Id_Area()] ) {
						//minim_area = cmax_area[area_cross.getValue().get_Id_Area()] ;
						AreaindexOfMin2 = area_cross1.getValue().get_Id_Area() ;
					}
				}
			}

			LinkedHashMap<Job, Machine> parent1_best_area = new LinkedHashMap<>();
			for(Entry<Job,Machine> pr : parent_1.get_chromosome().entrySet()) {
				if(pr.getValue().get_Id_Area() == AreaindexOfMin2 )
					parent1_best_area.put(pr.getKey(), pr.getValue()) ;
			}

			Iterator<Entry<Job, Machine>> area_iter1 = parent_1.get_chromosome().entrySet().iterator();
			Iterator<Entry<Job, Machine>> area_iter2 = parent_2.get_chromosome().entrySet().iterator();
			Iterator<Entry<Job, Machine>> area_best = parent1_best_area.entrySet().iterator();

			Entry<Job,Machine> pr2_iter = area_iter2.next();
			Entry<Job,Machine> best_iter = area_best.next();

			while(area_iter1.hasNext()) {
				Entry<Job,Machine> pr1_iter = area_iter1.next();

				if(pr1_iter.getKey().equals( best_iter.getKey()) ) {
					offspring_map.put(pr1_iter.getKey(), pr1_iter.getValue()) ;
					if(area_best.hasNext())
						best_iter = area_best.next();
				}else {
					while(parent1_best_area.containsKey( pr2_iter.getKey()) && area_iter2.hasNext() ) {
						pr2_iter = area_iter2.next();
					}
					if(!parent1_best_area.containsKey( pr2_iter.getKey())) {
						offspring_map.put(pr2_iter.getKey(), pr2_iter.getValue()) ;
					}
					if(area_iter2.hasNext())
						pr2_iter = area_iter2.next();
				}
			}

			break;

		default:
			break;
		}
		// return the offspring ( the child )
		return new Chromosome(offspring_map) ;
	}

	//================================= MUTATIONS ==============================================================//

	public Chromosome get_offspring_By_Mutation(Chromosome chrom, int type) {

		LinkedHashMap<Job, Machine> offspring_map_by_mutation = new LinkedHashMap<>();

		/*
		 * type 0: Swap mutation (exchange two elements into the chromozome)
		 */
		switch (type) {
		case 0:
			Random rdm = new Random();
			int random_job_1_mutation, random_job_2_mutation ;
			do {
				random_job_1_mutation = rdm.nextInt( chrom.get_chromosome().size() );
				random_job_2_mutation = rdm.nextInt( chrom.get_chromosome().size() );
			}
			while(random_job_1_mutation ==  random_job_2_mutation);



			//we take the iterator until position =rand1, then we replace the values (not key) from the values of parent 2
			int i=0 ;
			Iterator<Entry<Job, Machine>> iter_chrom_1 = chrom.get_chromosome().entrySet().iterator();
			while(iter_chrom_1.hasNext() && i<random_job_1_mutation) {
				iter_chrom_1.next();
				i++ ;
			}
			i=0 ;
			Iterator<Entry<Job, Machine>> iter_chrom_2 = chrom.get_chromosome().entrySet().iterator();
			while(iter_chrom_2.hasNext() && i<random_job_2_mutation) {
				iter_chrom_2.next();
				i++ ;
			}

			Entry<Job,Machine> Exchange_pair1 = iter_chrom_1.next();
			Entry<Job,Machine> Exchange_pair2 = iter_chrom_2.next();

			// build the offspring_map 
			Iterator<Entry<Job, Machine>> iter_chrom = chrom.get_chromosome().entrySet().iterator();
			i=0 ;
			while( iter_chrom.hasNext() ) {
				Entry<Job,Machine> pair = iter_chrom.next();
				if(i == random_job_1_mutation) {
					offspring_map_by_mutation.put( Exchange_pair2.getKey(), Exchange_pair2.getValue() ) ;
					i++ ;
				}
				else{
					if(i == random_job_2_mutation) {
						offspring_map_by_mutation.put( Exchange_pair1.getKey(), Exchange_pair1.getValue() ) ;
						i++ ;
					}
					else {
						offspring_map_by_mutation.put( pair.getKey(), pair.getValue() ) ;
						i++;	
					}
				}
			}
			break;

		default:
			break;
		}
		// return the offspring_by_mutation ( )
		return new Chromosome(offspring_map_by_mutation) ;	
	}
}


