/*
 * Author: Krišs Aleksandrs Vasermans
 * 201RDB301
 * RDBR0 / Intelektuālas robotizētas sistēmas
 */

import java.util.ArrayList;

//have to implement Cloneable so deep copies could be made
class Person implements Cloneable {
	int time;
	String pos, name;
	public Person(String name, int time, String pos) {
		this.name = name;
		this.time = time;
		this.pos = pos;
	}
	public String getName() {
		return this.name;
	}
	
	public String getPos() {
		return this.pos;
	}
	public void setPos(String pos) {
		this.pos = pos;
	}
	public int getTime() {
		return this.time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
	
	@Override
    public Person clone() {
        try {
            return (Person) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
	
}


class State {
	private boolean is_dead_end;
	private boolean is_target;
	private ArrayList<Person> P1 = new ArrayList<Person>();
	private ArrayList<Person> P2 = new ArrayList<Person>();
	private Person A, B, C, Torch;
	
	public State(Person A, Person B, Person C, Person Torch) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.Torch = Torch;
		

		if (A.getPos() == "P1") {
			P1.add(A);
		} else {
			P2.add(A);
		}
		
		if (B.getPos() == "P1") {
			P1.add(B);
		} else {
			P2.add(B);
		}
		
		if (C.getPos() == "P1") {
			P1.add(C);
		} else {
			P2.add(C);
		}
		
		if (Torch.getPos() == "P1") {
			P1.add(Torch);
		} else {
			P2.add(Torch);
		}
		this.is_target = this.checkTargetState();
		this.is_dead_end = this.checkDeadEnd();
	}	

	
	public boolean isTarget() {
		return this.is_target;
	}

	public boolean isDeadEnd() {
		return this.is_dead_end;
	}
	
	public Person getA() {
		return this.A;
	}
	public Person getB() {
		return this.B;
	}
	public Person getC() {
		return this.C;
	}
	public Person getTorch() {
		return this.Torch;
	}
	
	public ArrayList<Person> getP1_persons() {
		ArrayList<Person> P1_persons = new ArrayList<>();
		for (Person p: this.getP1()) {
			if (p.getName() != "Torch") {
				P1_persons.add(p);
			}
		}
		return P1_persons;
	}
	
	public ArrayList<Person> getP2_persons() {
		ArrayList<Person> P2_persons = new ArrayList<>();
		for (Person p: this.getP2()) {
			if (p.getName() != "Torch") {
				P2_persons.add(p);
			}
		}
		return P2_persons;
	}
	
	
	public ArrayList<Person> getP1() {
		return P1;
	}
	public ArrayList<Person> getP2() {
		return P2;
	}
	
	//the following methods check whether Node's state is the target
	//state and/or dead end (called from constructor)
	private boolean checkTargetState(State this) {
		//is target state if everyone is at P2 with time left on the torch
		if ((this.getP2().size() == 4) && (this.getTorch().getTime() >= 0)){
			return true;
		} else {
			return false;
		}
	}
	private boolean checkDeadEnd(State this) {
		/*
		 * dead end is when no moves are possible (any move time > torch time)
		 */
		if (this.isTarget()) {
			return true; 
		}//if state is target state, it's also considered a dead end
		if (this.getTorch().getPos() == "P1") {
			for (Person p :this.getP1_persons()) {
				if (p.getTime() > this.getTorch().getTime()) {
					return true; //is a dead end
				}	
			}
		}
		if (this.getTorch().getPos() == "P2") {
			for (Person p : this.getP2_persons()) {
				if (p.getTime() > this.getTorch().getTime()) {
					return true; //is a dead end
				}
			}
		}
		return false; // is not a dead end
	}

}

class Node {
	
	
	private State state;
	private Node parent;
	private ArrayList<Node> children;
	private boolean visited = false;
	
	public Node(State state) {
		this.state = state;
		this.children = new ArrayList<Node>();
	}
	public void addChild(Node child) {
		children.add(child);
		child.parent = this;
	}
	
	public ArrayList<Node> getChildren() {
		return this.children;
	}
	
	public State getState() {
		return this.state;
	}
	
	public Node getParent() {
		return this.parent;
	}
	public void setParent(Node parent) {
		this.parent = parent;
	}
	public void setVisited(boolean visited) {
		this.visited = visited;
	}
	public boolean isVisited() {
		return this.visited;
	}
	 //returns all combinations (ignoring order) of input array.
	private ArrayList<Person[]> getP1_possibilities(ArrayList<Person> arr) {
	        ArrayList<Person[]> combinations = new ArrayList<Person[]>();
	        
	        int n = arr.size();
	        
	        for (int i = 0; i<n - 1; i++) {
	            for (int j = i+1; j < n; j++) {
	                Person[] tmp = new Person[]{ arr.get(i), arr.get(j)};
	                combinations.add(tmp);
	            }
	        }
	        return combinations;
	    }
	 
	
	
	public int getSubTreeNodeCount(Node this, int... args) {
		int count;
		if (args.length == 0) {
			count = 0;
		} else {
			count = args[0] + 1;

		}
		ArrayList<Node> children = this.getChildren();
		for (int i=0; i< children.size(); i++) {
			count = children.get(i).getSubTreeNodeCount(count);
		}
		
		return count;
	}
	
	private String generateNodeInfo(Node this) {
		String P1 = "[P1:";
		String P2 = "[P2:";
		for (int i=0; i < this.state.getP1().size(); i++) {
			P1 += " " + this.state.getP1().get(i).getName() ;
			
			if (this.state.getP1().get(i).getName() == "Torch") {
				P1 += ":" + Integer.toString(this.state.getTorch().getTime());
			}
		}
		for (int i=0; i < this.state.getP2().size(); i++) {
			P2 += " " + this.state.getP2().get(i).getName() ;
			
			if (this.state.getP2().get(i).getName() == "Torch") {
				P2 += ":" + Integer.toString(this.state.getTorch().getTime());
			}
		}
		String nodeInfo =  P1 + "|" + P2 + "]";
		if (this.state.isDeadEnd()) {
			nodeInfo += "■";
		} 
		
		if (this.state.isTarget()) {
			nodeInfo += "✔";
		}
		return nodeInfo;
		
	}
	public void printTree(Node this, int... args) {
		int indentCount;
		if (args.length == 0) {
			indentCount = 0;
		} else {
			indentCount = args[0];
		}
		String indent = "";
		for (int i=0; i<indentCount; i++) {
			indent += "    ";
		}
		System.out.println(indent + this.generateNodeInfo());
		
		ArrayList<Node> children = this.getChildren();
		
		
		for (int i=0; i < children.size(); i++) {
			children.get(i).printTree(indentCount+1);
		}
		
	}
	
	public void generateTree(Node this) {
		//this method generates all possible children for given node
		this.generateChildren(); 
		
		for (Node child : this.getChildren()) {
			child.generateTree();
		}

	}
	
	private void generateChildren(Node this) {
		/* RULES:
		 * Three persons need to cross a bridge.
		 * They have a torch that lasts 12 minutes after being lit.
		 * Person A can cross the bridge in 1 minute, person B in 3
		 * and person C in 5 minutes.
		 * P1 - bridge start point
		 * P2 - bridge end
		 * P1 -> P2: two people must cross together along with the torch.
		 * Time it takes to cross is slowest person's crossing time
		 * P2 -> P1: one person must cross alone with the torch
		 * If the torch runs out of time, task is failed.
		 * If target state is reached with possible moves left, task is completed.
		 */

		if (this.state.isDeadEnd() || this.state.isTarget() || this.isVisited()) {
			return;
		}
		this.setVisited(true);
		
		//current node's state
		Person torch = this.state.getTorch();
		Person A = this.state.getA();
		Person B = this.state.getB();
		Person C = this.state.getC();
		
		//initialize variables for children
		Person newTorch = null;
		Person newA = null;
		Person newB = null;
		Person newC = null;
		
		int newTorchTime = 0;
		
		ArrayList<Person> curP1_persons = this.state.getP1_persons();
		ArrayList<Person> curP2_persons = this.state.getP2_persons();

		boolean moveFlag = false;
		
		if (torch.getPos() == "P1") { //move from P1 -> P2
			
			ArrayList<Person[]> possibilities = this.getP1_possibilities(curP1_persons);
			
			for (Person[] pair : possibilities) {
				
				boolean flagA = false;
				boolean flagB = false;
				boolean flagC = false;

				newTorchTime = torch.getTime() - Math.max(pair[0].getTime(), pair[1].getTime());
				for (Person p : pair) {
					
					if (newTorchTime >= 0) {
						moveFlag = true;
						
						//on every iteration a new copy has to be made
						newTorch = torch.clone();
						if (!flagA) newA = A.clone();
						if (!flagB) newB = B.clone();
						if (!flagC) newC = C.clone();
						
						if (p.getName() == "A") {
							newA = p.clone();
							newA.setPos("P2");
							flagA = true;
						}
						if (p.getName() == "B") {
							newB = p.clone();
							newB.setPos("P2");
							flagB = true;
						}
						if (p.getName() == "C") {
							newC = p.clone();
							newC.setPos("P2");
							flagC = true;
						}
						newTorch.setPos("P2");
						newTorch.setTime(newTorchTime);	
					}
					
				}
				if (moveFlag) {
					
					//create a node and add it as child to this node
					State newState = new State(newA, newB, newC, newTorch);
					Node newNode = new Node(newState);
					this.addChild(newNode);
					moveFlag = false;
				}
			
			}
			
		
		} else if (torch.getPos() == "P2") { //move P2->P1
	
			for (Person person : curP2_persons) {
				
				newTorch = torch.clone();
				newA = A.clone();
				newB = B.clone();
				newC = C.clone();
				
				newTorchTime = torch.getTime() - person.getTime();
			
					if (newTorchTime >= 0) {
						
						if (person.getName() == "A") {
							newA = person.clone();
							newA.setPos("P1");
						} 
						if (person.getName() == "B") {
							newB = person.clone();
							newB.setPos("P1");
						}
						if (person.getName() == "C") {
							newC = person.clone();
							newC.setPos("P1");
						} 
						newTorch.setPos("P1");
						newTorch.setTime(newTorchTime);	
						
						//create a node and add it as a child to this node
						State newState = new State(newA, newB, newC, newTorch);
						Node newNode = new Node(newState);
						this.addChild(newNode);
						moveFlag = false;
		
					}

			}
			
		}
	
	}
}


public class Main {

	public static void main(String[] args) {
		Person A = new Person("A", 1, "P1");
		Person B = new Person("B", 3, "P1");
		Person C = new Person("C", 5, "P1");
		Person Torch = new Person("Torch", 12, "P1"); //For simplicity Torch is also considered a person
		State startState = new State(A, B, C, Torch);
		
		Node root = new Node(startState);
		root.generateTree();
		System.out.print("Author: Krišs Aleksandrs Vasermans\n201RDB301\n");
		System.out.print("RDBR0 / Intelektuālas robotizētas sistēmas\\n\\n");
		System.out.println("■ - dead end\n✔ - target state\n");
		root.printTree();
		System.out.printf("Number of nodes: %d\n", root.getSubTreeNodeCount() + 1);
	}

}
