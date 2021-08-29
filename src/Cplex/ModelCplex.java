package Cplex;

import ilog.concert.*;
import ilog.cplex.*;


public class ModelCplex {

	public static void solveModel(DataForModel _data) throws InterruptedException  {

		DataForModel data = _data ;
		int M = data.getNumber_of_machines() ;
		int N = data.getNumber_of_jobs();
		int W= data.getNumber_of_areas() ;
		int B = data.getNumber_of_adjusters();

		int[][][] s = data.getSetup_Time_model() ;

		int[] U = data.getArea_limit_machines_model() ;

		int[][] p = data.getProcessTimes_On_Machines_model() ;	

		int[][] z = data.getIf_machine_on_area_model();		

		int[][] e = data.getIf_job_valid_for_machine_model() ;

		int H = data.getHorizon_for_model();

		int V = H ;
		//=============================================================================================//

		try {
			// définir un nouveau model 
			IloCplex model = new IloCplex();

			////////////////////////// VARIABLES DE DECISION ///////////////////////////////////////

			// les variables binaires x_mij : "i" est direcetment avant "j" sur la machine "m" ou pas  
			IloIntVar[][][] x = new IloIntVar[M][N+1][N+1];
			IloIntVar[][] y = new IloIntVar[M][N+1];
			for (int m=0; m<M;m++) {
				for(int i=0;i<N+1;i++) {
					y[m][i] = model.boolVar("y["+m+"]["+i+"]");
					for(int j=0;j<N+1;j++) {
						x[m][i][j] = model.boolVar("x["+m+"]["+i+"]["+j+"]");
						model.add(x[m][i][j]) ;
					}
				}
			}

			//les variables binaires y_mi : "i" est sur la machine "m" ou pas  
			/*	IloIntVar[][] y = new IloIntVar[M][N+1];
				for (int m=0; m<M; m++) {
					for(int i=1;i<N+1;i++) {
						y[m][i] = model.boolVar();
					}
				}

				// les variables binaires alpha_mit : "i" demarre a "t" sur la machine "m" ou pas
					IloIntVar[][][] alpha = new IloIntVar[M][N+1][H+1];
					for (int m=0; m<M; m++) {
						for(int i=0; i<N+1; i++) {
							for(int t=0;t<H+1;t++) {
								alpha[m][i][t] = model.boolVar();
							}
						}
					}  */


			// les variables binaires alpha_mit et beta_mit : "i" fini a "t" sur la machine "m" ou pas
			/*	IloIntVar[][][] alpha = new IloIntVar[M][N+1][H+1];
				IloIntVar[][][] beta = new IloIntVar[M][N+1][H+1];
				for (int m=0; m<M; m++) {
					for(int i=0; i<N+1; i++) {
						for(int t=0;t<H+1;t++) {
							alpha[m][i][t] = model.boolVar(); // alpha_mit
							beta[m][i][t] = model.boolVar();  // beta_mit
						}
					}
				}

				// les variables binaires delta_mijt : la configuration entre "i" et "j" commencent a "t" sur "m" ou pas
					IloIntVar[][][][] delta = new IloIntVar[M][N+1][N+1][H+1];
					for (int m=0; m<M; m++) {
						for(int i=0; i<N+1; i++) {
							for(int j=1; j<N+1; j++) {
								for(int t=0;t<H+1;t++) {
									delta[m][i][j][t] = model.boolVar() ;
								}
							}
						}
					} */

			// les variables binaires gamma_mijt : la configuration entre "i" et "j" fini a "t" sur "m" ou pas ET
			// les variables binaires alpha_mit et beta_mit : "i" fini a "t" sur la machine "m" ou pas
			IloIntVar[][][][] delta = new IloIntVar[M][N+1][N+1][H+1];
			IloIntVar[][][][] gamma = new IloIntVar[M][N+1][N+1][H+1];
			IloIntVar[][][] alpha = new IloIntVar[M][N+1][H+1];
			IloIntVar[][][] beta = new IloIntVar[M][N+1][H+1];
			for (int m=0; m<M; m++) {
				for(int i=0; i<N+1; i++) {
					for(int t=0;t<H+1;t++) {
						for(int j=1; j<N+1; j++) {
							delta[m][i][j][t] = model.boolVar("d["+m+"]["+i+"]["+j+"]["+t+"]") ; // delta_mijt
							gamma[m][i][j][t] = model.boolVar("g["+m+"]["+i+"]["+j+"]["+t+"]") ; // gamma_mijt
							model.add(delta[m][i][j][t]);
							model.add(gamma[m][i][j][t]);

						}
						alpha[m][i][t] = model.boolVar("a["+m+"]["+i+"]["+t+"]"); // alpha_mit
						beta[m][i][t] = model.boolVar("b["+m+"]["+i+"]["+t+"]");  // beta_mit
						model.add(alpha[m][i][t]);
						model.add(beta[m][i][t]);
					}
				}
			}

			// variable entiere : la fin de la tache "i": c_i
			IloIntVar[] c = new IloIntVar[N+1] ;
			for(int i=0;i<N+1;i++) {
				c[i] = model.intVar(0, H,"c["+i+"]");
				model.add(c[i]);
			}

			// la variable a minimiser c_max 
			IloIntVar c_max = model.intVar(0, H, "c[max]") ;
			model.add(c_max) ;

			////////////////////////// CONTRAINTES ////////////////////////////////////////////////

			/* Contraintes de precedence (sequencing) */

			//contrainte 2: unique tache a la 1ere place sur chaque machine "m": tache fictive "0" avant cette tache
			IloLinearIntExpr[] PremiereTache = new IloLinearIntExpr[M];
			for(int m=0; m<M;m++) {
				PremiereTache[m] = model.linearIntExpr();
				for(int j=1;j<N+1;j++) {
					PremiereTache[m].addTerm(1, x[m][0][j]);
				}
				model.addLe(PremiereTache[m] , 1) ; // (2)
			}

			//			for(int m=0; m<M;m++) {
			//				model.addLe(1, PremiereTache[m]) ; // (2)
			//			}

			// contrainte (3) Affectation des taches 
			IloLinearIntExpr[] AffectationTache = new IloLinearIntExpr[N+1];
			for(int i=1; i<N+1;i++) {
				AffectationTache[i] = model.linearIntExpr();
				for(int m=0; m<M;m++) {
					AffectationTache[i].addTerm(1, y[m][i]);
				}
				model.addEq(AffectationTache[i] , 1) ; // (3)
			}

			for(int m=0;m<M;m++) {
				model.addEq(y[m][0], 1) ; // Je l'ai ajouté pour les y[m][0] car le "0" est present sur tte les m ???
			}

			//			for(int i=1;i<N+1;N++) {
			//				model.addEq(AffectationTache[i] , 1) ; // (3)
			//			}

			// contrainte (4) au plus un successeur 
			IloLinearIntExpr[][] Successeur = new IloLinearIntExpr[M][N+1];
			for(int m=0; m<M;m++) {
				for(int i=1; i<N+1;i++) {
					Successeur[m][i] = model.linearIntExpr();
					for(int j=1;j<N+1;j++) {
						if(j!= i) {
							Successeur[m][i].addTerm(1, x[m][i][j]);
						}
					}
					model.addLe(Successeur[m][i] , y[m][i]) ; // (4)
				}
			}

			//			for(int m=0; m<M;m++) {
			//				for(int i=1;i<N+1;i++) {
			//					model.addLe(Successeur[m][i] , y[m][i]) ; // (4)
			//				}
			//			}


			// contrainte (5) au plus un predecesseur 
			IloLinearIntExpr[][] Predeccesseur = new IloLinearIntExpr[M][N+1];
			for(int m=0; m<M;m++) {
				for(int j=1; j<N+1;j++) {
					Predeccesseur[m][j] = model.linearIntExpr();
					for(int i=0;i<N+1;i++) {
						if(i != j) {
							Predeccesseur[m][j].addTerm(1, x[m][i][j]);
						}
					}
					model.addLe(y[m][j] , Predeccesseur[m][j]) ; // (5)
				}
				model.addEq(alpha[m][0][0], 1) ; // (6)
				model.addEq(beta[m][0][0],  1) ;  // (7)
			}

			//			for(int m=0; m<M;m++) {
			//				for(int j=0;j<N+1;j++) {
			//					model.addLe(y[m][j] , Predeccesseur[m][j]) ; // (5)
			//				}
			//			}

			/* INITIALISATION */

			//			for(int m=0; m<M;m++) { 
			//				model.addEq(alpha[m][0][0], 0) ; // (6)
			//				model.addEq(beta[m][0][0], 0) ;  // (7)
			//			}

			/* Contraintes de temps */

			/*
			 * contrainte 8 : chaque tache "i" commence exactement une fois sur un "t" dans 0-H (= y_mi)
			 * contrainte 9 : chaque tache "i" fini exactement une fois sur un "t" dans 0-H (= y_mi)
			 */

			IloLinearIntExpr[][] StartTache = new IloLinearIntExpr[M][N+1];
			IloLinearIntExpr[][] FinishTache = new IloLinearIntExpr[M][N+1];

			//ses 2 contraintes sont pour les contraintes 13,14 
			IloLinearIntExpr[][] StartTacheTemps = new IloLinearIntExpr[M][N+1];
			IloLinearIntExpr[][] FinishTacheTemps = new IloLinearIntExpr[M][N+1];

			for(int m=0; m<M;m++) {
				for(int i=0; i<N+1;i++) { /////////?????? c'est i=1 dans le modele mais j'ai mis i=0 pour 13 et 14
					StartTache[m][i] = model.linearIntExpr();
					FinishTache[m][i] = model.linearIntExpr();
					StartTacheTemps[m][i] = model.linearIntExpr();
					FinishTacheTemps[m][i] = model.linearIntExpr();
					for(int t=0;t<H+1;t++) {
						StartTache[m][i].addTerm(1, alpha[m][i][t]);
						FinishTache[m][i].addTerm(1, beta[m][i][t]);
						StartTacheTemps[m][i].addTerm(t, alpha[m][i][t]);
						FinishTacheTemps[m][i].addTerm(t, beta[m][i][t]);
					}
					if(i != 0) {
						model.addEq( StartTache[m][i], y[m][i]) ; // (8)
						model.addEq( FinishTache[m][i], y[m][i]); // (9)

						//A VERIFIER ???????????
						model.addLe( model.sum(FinishTache[m][i] , 
								model.prod(-1, StartTache[m][i])) , model.sum( 1 , model.prod(-1, y[m][i]))); // (10)
					}
				}					
			}

			//			for(int m=0; m<M;m++) {
			//				for(int i=1;i<N+1;i++) {
			//					model.addEq( StartTache[m][i], y[m][i]) ; // (8)
			//					model.addEq( FinishTache[m][i], y[m][i]); // (9)
			//
			//					// Contrainte 10 : Pour accelerer la resolution (la meme que les 2 precedentes)
			//					//A VERIFIER ???????????
			//					model.addLe( model.sum(FinishTache[m][i] , 
			//							model.prod(-1, StartTache[m][i])) , model.sum( 1 , model.prod(-1, y[m][i]))); // (10)				
			//				}
			//			}


			/*
			 *  Contrainte 11 : : debaut de la configuration entre "i" et "j" a "t" 
			 *  Contrainte 12 : : Fin de la configuration entre "i" et "j" a "t" 
			 */

			IloLinearIntExpr[][][] ConfigSetupDebut = new IloLinearIntExpr[M][N+1][N+1];
			IloLinearIntExpr[][][] ConfigSetupFin = new IloLinearIntExpr[M][N+1][N+1];
			// les 2 suivantes sont pour les contraintes 13 et 14
			IloLinearIntExpr[][][] ConfigSetupDebutTemps = new IloLinearIntExpr[M][N+1][N+1];
			IloLinearIntExpr[][][] ConfigSetupFinTemps = new IloLinearIntExpr[M][N+1][N+1];

			for(int m=0; m<M;m++) {
				for(int i=0; i<N+1;i++) {
					for(int j=1;j<N+1;j++) {

						ConfigSetupDebut[m][i][j] = model.linearIntExpr();
						ConfigSetupFin[m][i][j] = model.linearIntExpr();
						ConfigSetupDebutTemps[m][i][j] = model.linearIntExpr();
						ConfigSetupFinTemps[m][i][j] = model.linearIntExpr(); 

						for(int t=0;t<H+1;t++) {
							ConfigSetupDebut[m][i][j].addTerm(1, delta[m][i][j][t]);
							ConfigSetupFin[m][i][j].addTerm(1, gamma[m][i][j][t]);
							ConfigSetupDebutTemps[m][i][j].addTerm(t, delta[m][i][j][t]);
							ConfigSetupFinTemps[m][i][j].addTerm(t, gamma[m][i][j][t]);
						}
					}
				}
			}

			for(int m=0; m<M;m++) {
				for(int i=0; i<N+1;i++) {
					for(int j=1;j<N+1;j++) {
						model.addEq( ConfigSetupDebut[m][i][j] , x[m][i][j] ) ; // (11)
						model.addEq( ConfigSetupFin[m][i][j]   , x[m][i][j] ) ; // (12)
					}
				}
			}

			/*
			 * Contrainte 13 : la config entre "i" et "j" doit commencer apres la fin de "i" (apres beta_mit) 
			 * Contrainte 14 : la config entre "i" et "j" doit finir  avant le debut de "j" (apres alpha_mjt)
			 */

			for(int m=0; m<M;m++) {
				for(int i=0; i<N+1;i++) {
					for(int j=1;j<N+1;j++) {

						model.addLe( FinishTacheTemps[m][i] , 
								model.sum( ConfigSetupDebutTemps[m][i][j] ,   
										model.prod(V, model.sum(1, model.prod(-1, x[m][i][j]))) ) ); // (13)

						model.addLe( ConfigSetupFinTemps[m][i][j] , 
								model.sum( StartTacheTemps[m][j] ,   
										model.prod(V, model.sum(1, model.prod(-1, x[m][i][j]))) ) ); // (14)

					}					
				}
			}

			/////////////////////////// Contraintes de durees des taches  ///////////////////////////////////

			/*
			 * Contrainte 15 : respecter la duree de la tache <=> (fin de "i" - debut de "i") == "p_mi"   
			 */

			for(int m=0; m<M;m++) {
				for(int i=1;i<N+1;i++) {
					model.addEq( model.sum( FinishTacheTemps[m][i] , model.prod(-1, StartTacheTemps[m][i]) ) , 
							model.prod(p[m][i], y[m][i]) ) ; // (15)
				}
			}

			/*
			 * Contrainte 16 : respecter la duree de la tache <=> (fin de "i" - debut de "i") == "p_mi"   
			 */

			for(int m=0; m<M;m++) {
				for(int i=0; i<N+1;i++) {
					for(int j=1;j<N+1;j++) {

						model.addLe(
								model.sum( ConfigSetupFinTemps[m][i][j] , model.prod(-1, ConfigSetupDebutTemps[m][i][j])),
								model.sum( model.sum( StartTacheTemps[m][j] , model.prod(-1, FinishTacheTemps[m][i] )),
										model.prod(V, model.sum(1, model.prod(-1, x[m][i][j])))) ) ;  // (16)


						model.addEq( model.sum( ConfigSetupFinTemps[m][i][j] , model.prod(-1, ConfigSetupDebutTemps[m][i][j])),
								model.prod( s[m][i][j] , x[m][i][j] ) );  // (17)

					}
				}
			}

			/* Contraintes de ressources  */

			/*
			 * Contrainte 18: Nombre de machine Max "U_w" pouvant fonctionner en mm temps dans une zone "w"
			 */
			IloLinearIntExpr[][] Start_Finish = new IloLinearIntExpr[H+1][W];

			for(int t=0;t<H+1;t++) {
				for(int w=0;w<W;w++) {

					Start_Finish[t][w] = model.linearIntExpr();

					for(int m=0; m<M;m++) {
						for(int i=1; i<N+1;i++) {
							for(int to=0;to<t+1;to++) {
								Start_Finish[t][w].addTerm(z[m][w] , alpha[m][i][to]);
								Start_Finish[t][w].addTerm(-z[m][w] , beta[m][i][to]) ;	 
							}
						}
					}
					model.addLe( Start_Finish[t][w] , U[w]) ; // (18)
				}
			}

			/*
			 * Contrainte 19: Ne pas dépasser le nombre de ressources dispo "B" 
			 * 			(on peut pas configurer au-dela de B au mm temps  )
			 */


			IloLinearIntExpr[] Setup_Start_Finish = new IloLinearIntExpr[H+1];

			for(int t=0;t<H+1;t++) {

				Setup_Start_Finish[t]= model.linearIntExpr() ;

				for(int m=0; m<M;m++) {
					for(int i=0; i<N+1;i++) {
						for(int j=1;j<N+1;j++) {
							for(int to=0;to<t+1;to++) {
								Setup_Start_Finish[t].addTerm(1 , delta[m][i][j][to]);
								Setup_Start_Finish[t].addTerm(-1 , gamma[m][i][j][to]) ;	 
							}
						}
					}
				}
				model.addLe( Setup_Start_Finish[t] , B) ; // (19)
			}

			/*
			 * Contrainte 20: la tache "i" peut etre ordonnée sur la machine "m" uniquement 
			 * si la tache "i" est éligible pour la machine "m" ( si e_mi == 1 )
			 */

			for(int m=0; m<M;m++) {
				for(int i=1;i<N+1;i++) {
					model.addLe(y[m][i], e[m][i-1]) ;
				}
			}

			/* Objective function calculation constraints */

			/*
			 * Contrainte 21: Calcul de la fin de la tache "i" => c_i
			 * Contrainte 22: La fin de la tache facultative "o" => c_0 == 0 (elle commence et fini a 0)
			 * Contrainte 23: c_max >= c[i] pour tout les "i"
			 */

			IloLinearIntExpr[] FinTache = new IloLinearIntExpr[N+1];

			for(int i=1;i<N+1;i++) {
				FinTache[i]= model.linearIntExpr() ;
				for(int m=0; m<M;m++) {					
					for(int t=0;t<H+1;t++) {
						FinTache[i].addTerm(t , beta[m][i][t]);	 
					}
				}
				model.addEq( c[i] , FinTache[i] ) ; // (21)
				model.addGe(c_max, c[i]) ; // (23)
			}

			model.addEq( c[0] , 0 ) ; // (22)

			/*
			 * Contrainte 23: c_max >= c[i] pour tout les "i"
			 */

			//			for(int i=1;i<N+1;i++) {
			//				model.addGe(c_max, c[i]) ; // (23)
			//			}

			////////////////////////// FONCTION OBJECTIVE ///////////////////////////////////////


			model.addMinimize(c_max);
			model.exportModel("model1.lp") ;
			
			
			
			////////////////////////// SOLVE MODEL ///////////////////////////////////////

			if (model.solve()) {
				System.out.println("\nC_max = "+model.getObjValue() + "\n");
				//model.getValues(c);
				for(int m=0; m<M;m++) {
					for(int i=0; i<N+1 ; i++) {
						for(int j=0;j<N+1;j++) {
							//System.out.println("c[" +i +"] ="+ model.getValue(c[i]) );
							if( model.getValue(x[m][i][j]) != 0) {
								//System.out.println("alpha["+m+"]["+i+"]["+j+"] = " + model.getValue(alpha[m][i][j]) );
								System.out.println("x["+m+"]["+i+"]["+j+"] = " + model.getValue(x[m][i][j])+" la tache "+i+" sera juste avant la tache "+j+" sur la machine "+(m+1) );
								//System.out.println("y[" +m +"]["+ i +"] ="+ model.getValue(y[m][i]) ) ;
							}		
						}
						//System.out.println("y[" +m +"]["+ i +"] ="+ model.getValue(y[m][i]) );
					}
				}
				System.out.println("le nombre de variables est: " + model.getNintVars()+model.getNbinVars() );
				//System.out.println("le nombre de variables boolenne est: " );
				System.out.println("le nombre de contraintes est: " + model.getNrows() );
				//model.writeSolutions("solution.lp") ;
				System.out.println("H = "+H);
			}
			else {
				System.out.println("problem not solved");
			}

			model.end();



		} catch (IloException exp) {
			exp.printStackTrace();
		}

	}
}

