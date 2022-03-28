package transferenciadadostp;

public class teste {
    public static void main(String[] args){
        int[] vetor1 = {1, 2, 3, 4, 4, 5, 6};
        int[] vetor2 = {5, 6, 7, 8};
        vetor2 = vetor1;  //vetor sobreescreve ou add ao final?
        for(int i = 0; i < vetor1.length; i++) System.out.print(vetor2[i]); // sobreescreve
    }
}
