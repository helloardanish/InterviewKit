import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GraphImpl {
    public static void main(String[] args) {
        // adjacency matrix
        /*
        2D array to store 1's and 0's (can be represented using true and false)
        
        time complexity : O(1)
        space complexity : O(v^2)

        */

        GraphM graph = new GraphM(5);

        graph.addNode(new Node('A'));
        graph.addNode(new Node('B'));
        graph.addNode(new Node('C'));
        graph.addNode(new Node('D'));
        graph.addNode(new Node('E'));

        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(4, 0);
        graph.addEdge(4, 2);

        graph.print();

        System.out.println(graph.checkEdge(0, 1));
        System.out.println(graph.checkEdge(3, 2));

        // adjacency list
        /*
        
        An arraylist of linked list
        Each linkedlist has a unique node at the head
        All adjacent neighbours to that node are added to that node's linkedlist

        runtime to check edge O(V)
        space complexity : O(V+E)
        */

        GraphA graphA = new GraphA();
        graphA.addNode(new Node('A'));
        graphA.addNode(new Node('B'));
        graphA.addNode(new Node('C'));
        graphA.addNode(new Node('D'));
        graphA.addNode(new Node('E'));

        graphA.addEdge(0, 1);
        graphA.addEdge(1, 2);
        graphA.addEdge(1, 4);
        graphA.addEdge(2, 3);
        graphA.addEdge(2, 4);
        graphA.addEdge(4, 0);
        graphA.addEdge(4, 2);


        graphA.print();

    }
}

class GraphM{
    List<Node> nodes;

    int[][] matrix;
    GraphM(int size){
        nodes = new ArrayList<>();
        matrix = new int[size][size];
    }

    public void addNode(Node node){
        nodes.add(node);
    }

    public void addEdge(int source, int desination){
        matrix[source][desination] = 1;
    }

    public boolean checkEdge(int source, int desination){
        return matrix[source][desination]==1 ? true : false;
    }

    public void print(){
        System.out.print("  ");
        for(Node n: nodes){
            System.out.print(n.data + " ");
        }
        System.out.println();
        for(int i=0; i<matrix.length; i++){
            System.out.print(nodes.get(i).data+ " ");
            for(int j=0; j<matrix[i].length; j++){
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
    }
}


class GraphA{
    
    List<LinkedList<Node>> nodes;
    GraphA(){
        nodes = new ArrayList<>();
    }

    public void addNode(Node node){
        LinkedList<Node> currList = new LinkedList<>();
        currList.add(node);

        nodes.add(currList);
    }

    public void addEdge(int source, int desination){
        nodes.get(source).add(nodes.get(desination).get(0));
    }

    public boolean checkEdge(int source, int desination){
        LinkedList<Node> currList = nodes.get(source);
        Node destNode = nodes.get(desination).get(0);
        for(Node n: currList){
            if(n==destNode){
                return true;
            }
        }
        return false;
    }

    public void print(){
        for(LinkedList<Node> ln: nodes){
            for(Node n: ln){
                System.out.print(n.data+" -> ");
            }
            System.out.println();
        }
    }
}


        // List<Node> nodes

class Node{
    char data;
    Node(char data){
        this.data = data;
    }
}