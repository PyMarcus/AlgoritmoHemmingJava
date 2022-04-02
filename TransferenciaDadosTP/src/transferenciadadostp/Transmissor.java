package transferenciadadostp;

import java.util.Random;

public class Transmissor {

    private String mensagem;

    // Cores para exibição personalizada das saídas de texto na tela do usuário
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE

    //construtor do transmissor
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
    }

    private int calculaQuantidadeNumeroHamming(int k){
        /*
        Método que resolve a inequação de hamming, com base em uma proporção de 2.5.
        Logo, através desse método, pode-se encontrar o número de hamming necessário.

        :parametros: int
        :retorno: int
         */

        int referencia = 4, padrao = 8;  // referência conhecida para padrão no cálculo.

        if(k >= 2.4 * padrao && k <= padrao * 2.5) referencia += 1;
        if(k == 1) referencia = 2;
        if(k == 2 || k == 3 || k == 4) referencia = 3;
        if(k >= 5 && k < padrao) referencia = 4;
        return referencia;
    }
    
    private boolean[] dadoBitsHemming(boolean[] bits){
        /*
            Recebe 8 bits de cada caractere, ou seja, um byte,conforme definido na tabela ASCII
            Após isso,acrescenta os números de hamming,em quantidade
            determinada pelo método implementado calculaQuantidadeNumeroHamming(4 bits)e devolve o vetor modificado(12 bits).

            :parametro: boolean[]
            :retorno: boolean[]
        */

        // impressão dos bits originais, ou seja, sem o acréscimo de hamming
        System.out.println(BLUE_BOLD + "\nBits sem acréscimo do número de hamming: ");
        for(int i = 0; i < bits.length; i++) System.out.print(bits[i] + " ");

        // inclui numero de hamming e os bits
        boolean[] hamming = new boolean[12];

        //define quantidade de números de hamming
        int quantidade_hamming = calculaQuantidadeNumeroHamming(bits.length);

        /*
        Gera vetor equivalente com as potências de 2,afim de armazenar os bits de hamming nos locais definidos com base
        nos valores das contidos neste vetor, após o incremento.
        */
        int[] potencias_2 = new int[quantidade_hamming];
        for(int i = 0; i < quantidade_hamming; i++){
            potencias_2[i] = (int) Math.pow(2, i);
        }

        // operação XOR com os valores contidos no vetor bits que irão determinar o número de hamming, em si.
        boolean h1 = bits[0] ^ bits[1] ^ bits[3] ^ bits[4] ^ bits[6];
        boolean h2 = bits[0] ^ bits[2] ^ bits[3] ^ bits[5] ^ bits[6];
        boolean h3 = bits[1] ^ bits[2] ^ bits[3] ^ bits[7];
        boolean h4 = bits[4] ^ bits[5] ^ bits[6] ^ bits[7];

        boolean[] hamming_vector = {h1, h2, h3, h4}; // vetor que armazena o resultado do número de hamming

        /*
         O loop, abaixo, adiciona os números de hamming nas posições coincidentes às potências de 2
         Para isso, utiliza-se contadores auxiliares para servir de índices onde, se o valor de i for equivalente
         às potência de 2 definidas no vetor.
         */
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

        // impressão com os números de hamming acrescidos
        System.out.println(YELLOW_BOLD + "\nAcrescido bits de hamming: ");
        for(int i = 0; i < hamming.length; i++) System.out.print(armazena[i] + " ");

        bits = armazena;  // bits é sobreescrito pelo vetor que contém os números de hamming

        return bits;
    }

    public void enviaDado(Receptor receptor){

        for(int i = 0; i < this.mensagem.length();i++){
            boolean bits[] = streamCaracter(this.mensagem.charAt(i));  // alimenta o vetor com cada byte de cada caractere

            boolean[] bitsHemming = dadoBitsHemming(bits);

            //add ruidos à mensagem que será enviada para o receptor(servidor)
            System.out.println(RED_BOLD + "\nDados com ruídos: " );
            geradorRuido(bitsHemming);
            for(int z = 0; z < bitsHemming.length; z ++){
                System.out.print(bitsHemming[z] + " ");  // impressão do vetor, possivelmente, alterado pelo ruído.
            }
            System.out.println();

            //enviando a mensagem "pela rede" para o receptor
            receptor.receberDadoBits(bitsHemming);
        }
    }
}