package transferenciadadostp;

import java.util.Random;

public class Transmissor {
    private String mensagem;
    // Bold
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    //construtor do transmisspr
    public Transmissor(String mensagem) {
        this.mensagem = mensagem;
    }
    
    //convertendo um símbolo para "vetor" de boolean (bits)
    private boolean[] streamCaracter(char simbolo){
        
        //cada símbolo da tabela ASCII é representado com 8 bits
        boolean bits[] = new boolean[8];
        
        //convertendo um char para int (encontramos o valor do mesmo na tabela ASCII)
        int valorSimbolo = (int) simbolo;
        int indice = 7;
        
        //convertendo cada "bits" do valor da tabela ASCII  [com cada letra convertida em inteiro, converte-se em binário]
        while(valorSimbolo >= 2){
            int resto = valorSimbolo % 2;
            valorSimbolo /= 2;
            bits[indice] = (resto == 1);  // aqui converte de binario pra boolean
            indice--;
        }

        bits[indice] = (valorSimbolo == 1);
        
        return bits;
    } 
    
    //não modifique (seu objetivo é corrigir esse erro gerado no receptor)
    private void geradorRuido(boolean[] bits){

        Random geradorAleatorio = new Random();
        
        //pode gerar um erro ou não..
        if(geradorAleatorio.nextInt(5) > 1){
            int indice = geradorAleatorio.nextInt(8); // recebe uma posicao para ser modificada no vetor
            bits[indice] = !bits[indice];  // inverte o valor do vetor naquela posição.
        }
        System.out.println();
        System.out.println(RED_BOLD + "Dados com ruídos: " );
        for(int i = 0; i < bits.length; i ++){
            System.out.print(bits[i] + " ");
        }
        System.out.println();
    }
    private int calculaQuantidadeNumeroHamming(int k){
        // percebi, que há um aumento de 1 em m, proporcionalmente a 2.5
        /*
        Calcula numero de hamming necessário
         */
        int referencia = 4;
        int padrao = 8;
        if(k >= 2.4 * padrao && k <= padrao * 2.5) referencia += 1;
        if(k == 1) referencia = 2;
        if(k == 2 || k == 3 || k == 4) referencia = 3;
        if(k >= 5 && k < padrao) referencia = 4;
        return referencia;
    }
    
    private boolean[] dadoBitsHemming(boolean[] bits){


        // recebe o booleano equivalente a letra
        /*
        OBS recebe 8 bits de cada letra, ou seja, um byte,conforme a tabela ASCII
            Acrescenta números de hamming,e devolve o vetor. Números a ser inseridos: 4,
            números recebidos: 8, total ao final: 12bits
        */
        System.out.println();
        System.out.println(BLUE_BOLD + "Bits sem acréscimo do número de hamming: ");
        for(int i = 0; i < bits.length; i++) System.out.print(bits[i] + " ");
        // inclui numero de hamming e os bits
        boolean[] hamming = new boolean[12];

        //define quantidade de numeros de hamming
        int quantidade_hamming = calculaQuantidadeNumeroHamming(bits.length);

        //gera vetor equivalente com as potencias de 2 para armazenar conforme as posicoes
        int[] potencias_2 = new int[quantidade_hamming];
        for(int i = 0; i < quantidade_hamming; i++){
            potencias_2[i] = (int) Math.pow(2, i);
        }
        boolean h1 = bits[0] ^ bits[1] ^ bits[3] ^ bits[4] ^ bits[6];
        boolean h2 = bits[0] ^ bits[2] ^ bits[3] ^ bits[5] ^ bits[6];
        boolean h3 = bits[1] ^ bits[2] ^ bits[3] ^ bits[7];
        boolean h4 = bits[4] ^ bits[5] ^ bits[6] ^ bits[7];

        boolean[] hamming_vector = {h1, h2, h3, h4}; // hamming e o de 8 bits
        //System.out.println("h1: " + h1 + "h2: " + h2 + "h3: " + h3 + "h4: " + h4);
        // adiciona numero de hamming na posicao de potencia de 2:
        int contador = 0;
        int contador2 = 0;
        int contador3 = 0;
        boolean[] armazena = new boolean[hamming.length];
        for(int i = 1; i <= hamming.length; i++){
            if(i == potencias_2[contador]){

                armazena[contador3] = hamming_vector[contador];
                contador3 ++;
                hamming[contador] = hamming_vector[contador];
                if(contador < potencias_2.length - 1) contador ++;
            }else{

                armazena[contador3] = bits[contador2];
                contador3 ++;
                hamming[contador2] = bits[contador2];
                contador2 ++;
            }
        }
        // impressão
        System.out.println(YELLOW_BOLD + "\nAcrescido bits de hamming: ");
        for(int i = 0; i < hamming.length; i++){
            System.out.print(armazena[i] + " ");
        }
        bits = armazena;  // bits recebe o vetor com número de hamming

        return bits;
    }
    // i = 01101001
    
    public void enviaDado(Receptor receptor){

        for(int i = 0; i < this.mensagem.length();i++){
            boolean bits[] = streamCaracter(this.mensagem.charAt(i));  // alimenta o vetor com um cada byte de cada letra


            /*-------AQUI você deve adicionar os bits de Hemming para contornar os problemas de ruidos
                        você pode modificar o método anterior também
                boolean bitsHemming[] = dadoBitsHemming(bits);
            */
            boolean[] bitsHemming = dadoBitsHemming(bits);

            //add ruidos na mensagem a ser enviada para o receptor
            geradorRuido(bitsHemming); //substituir pelo bitdehamming

            //enviando a mensagem "pela rede" para o receptor
            receptor.receberDadoBits(bitsHemming);
        }
    }
}
