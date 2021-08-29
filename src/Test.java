
public class Test {

	public static void main(String[] args) {
		int[][][] s = new int[2][3][3] ; 
		for(int m=0; m<2; m++) {
			for(int i=0; i<3; i++) {
				for(int j=0; j< 3; j++) {
					s[m][i][j] = 1;
				}	
			}
		}

		int[][][] ss = new int[2][4][4] ;
		for(int m=0; m<2; m++) {
			for(int i=1; i<4; i++) {
				for(int j=0; j<4; j++) {
					if(j==0) {
						ss[m][i][j] = Integer.MAX_VALUE ;
					}else {
						ss[m][i][j] =s[m][i-1][j-1];
					}
				}	
			}
		}
		for(int m=0; m<2; m++) {
			for(int i=0; i<4; i++) {
				for(int j=0; j<4; j++) {
					System.out.println(ss[m][i][j] );
				}	
				System.out.println("\n");
			}
		}

		/////////////////////////////////////////////////////////////////////////////////////
		System.out.println("---------------------------------------------------------------");






	}

}
