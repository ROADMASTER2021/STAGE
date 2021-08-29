package GeneticPackage;
import java.util.ArrayList;


public class Job {

	private Integer Id_Job ; 
	private ArrayList<ArrayList<Integer>> ProcessTimes_On_Machines ; // Run time of this job on all machines 
	private Integer Start_Job;
	private Integer Id_predecessor ;
	private Integer Id_successor ;


	public Job(Integer _id, ArrayList<ArrayList<Integer>> _ProcessTimes_On_Machines /*, Integer _Nbre_Machine*/) {
		this.Id_Job = _id ; 
		this.ProcessTimes_On_Machines = new ArrayList<ArrayList<Integer>>(_ProcessTimes_On_Machines);
	}

	public Job(Job j) { 
		this.Id_Job = j.Id_Job ;
		this.ProcessTimes_On_Machines = new ArrayList<ArrayList<Integer>>(j.ProcessTimes_On_Machines) ;
	}


	public Integer get_Id() {
		return Id_Job;
	}

	public Integer getStart_Job() {
		return Start_Job;
	}

	public void setStart_Job(Integer start_Job) {
		Start_Job = start_Job;
	}

	public Integer getId_successor() {
		return Id_successor;
	}

	public void setId_successor(Integer id_successor) {
		Id_successor = id_successor;
	}

	public Integer getId_predecessor() {
		return Id_predecessor;
	}

	public void setId_predecessor(Integer id_predecessor) {
		Id_predecessor = id_predecessor;
	}

	public ArrayList<ArrayList<Integer>> getProcessTimes_On_Machines() {
		return ProcessTimes_On_Machines;
	}

	public void setProcessTimes_On_Machines(ArrayList<ArrayList<Integer>> processTimes_On_Machines) {
		ProcessTimes_On_Machines = processTimes_On_Machines;
	}

	// return RunTime of job on different machines in order (0-->M) (when it's null => machine is not valid for this job)
	public ArrayList<ArrayList<Integer>> get_ProcessTimes_On_Machines() {
		return ProcessTimes_On_Machines;
	}


	public Integer get_Nbr_Machine() {
		return this.ProcessTimes_On_Machines.size();
	}


	//return a List of just valid machines for this job (don't take machines on which runtime of job in the table == null)
	public ArrayList<Integer> get_Id_Valid_Machines() {
		ArrayList<Integer> Id_Valid_Machines = new ArrayList<Integer>() ;
		int j = 0 ; 
		while(j < this.ProcessTimes_On_Machines.size()) {
			if (this.If_Machine_is_valid(j) == true) {
				Id_Valid_Machines.add(j) ;
				j++;
			}else {
				j++;
			}
		}

		return Id_Valid_Machines ;
	}


	// return a RunTime of this job on a specified machine " IdMachine "
	public Integer get_ProcessTime_On_Machine(int IdMachine) { 

		return this.ProcessTimes_On_Machines.get(IdMachine).get(1) ;

		/*because in the table (2 dim) the first line matches with all machines (so this.Pro....Machine[0] == All machines)
		 Valid AND not Valid*/
	}


	// can this job work on machine "_Id_Machine"
	public boolean If_Machine_is_valid(int _Id_Machine) {

		assert _Id_Machine < this.ProcessTimes_On_Machines.size() ; // pas utilisé 
		assert _Id_Machine >= 0 ; // Pareil

		return this.get_ProcessTime_On_Machine(_Id_Machine) != null ;
	}


	@Override
	public String toString() {
		return "Job[" + Id_Job +"]";
	}

	public String toString_with_processTimes_on_machines() {
		return "Job[" + Id_Job + ", ProcessTimes_On_Machines = " + ProcessTimes_On_Machines;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Id_Job == null) ? 0 : Id_Job.hashCode());
		result = prime * result + ((ProcessTimes_On_Machines == null) ? 0 : ProcessTimes_On_Machines.hashCode());
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
		Job other = (Job) obj;
		if (Id_Job == null) {
			if (other.Id_Job != null)
				return false;
		} else if (!Id_Job.equals(other.Id_Job))
			return false;
		return true;
	}






}
