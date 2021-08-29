package GeneticPackage;

public class Machine {

	private Integer Id_Machine ;
	private Integer Id_Area ;

	public Machine(Integer _IdMachine) {
		this.Id_Machine = _IdMachine ;
		
	}
	public Machine(Machine machine) {
		this.Id_Machine = machine.Id_Machine ;
		set_Id_Area(machine.Id_Area); 
	}


	public Machine(Integer _IdMachine , Integer _IdArea) {
		this.Id_Machine = _IdMachine ;
		set_Id_Area(_IdArea) ;		
	}


	public void set_Id_Area(Integer _IdArea) {
		this.Id_Area = _IdArea;
	}


	public Integer get_Id_Machine() {
		return Id_Machine;
	}


	public Integer get_Id_Area() {
		return Id_Area;
	}


	@Override
	public String toString() {
		return "Machine[" + Id_Machine + "]";
	}

	public String toString_with_areaId() {
		return "Machine[" + Id_Machine + ", IdArea = " + Id_Area + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Id_Area == null) ? 0 : Id_Area.hashCode());
		result = prime * result + ((Id_Machine == null) ? 0 : Id_Machine.hashCode());
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
		Machine other = (Machine) obj;
		if (Id_Machine == null) {
			if (other.Id_Machine != null)
				return false;
		} else if (!Id_Machine.equals(other.Id_Machine))
			return false;
		return true;
	}


}
