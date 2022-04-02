package transferenciadadostp;

public class Receptor {

    // Cores para exibição personalizada das saídas de texto na tela do usuário
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN


    //mensagem recebida pelo transmissor
    private String mensagem;

    public Receptor() {
        //mensagem vazia no início da execução
        this.mensagem = "";
    }
    
    public String getMensagem() { return mensagem; }
 
    private void decodificarDado(boolean bits[]){
        /*
        Método alterado para exibição da mensagem original, foi reescrito no intuito de acrescentar uma melhoria
        que é a exibição de palavras acentuadas também.
        :parametro: boolean[]
        :return: void
        */
        int codigoAscii = 0;
        int expoente = bits.length-1;

        int[] bits_ = new int[bits.length];
        int count = 0;

        // Converte de booleano para binário.
        for(boolean bit_ : bits){
            if(bit_){
                bits_[count] = 1;
            }else{
                bits_[count] = 0;
            }
            count ++;
        }

        // binario original: 1 byte
        int[] binarios_originais = {bits_[2],bits_[4], bits_[5], bits_[6], bits_[8], bits_[9], bits_[10], bits_[11]};

        String new_binario = "";
        // preenche o vetor new binario com os bits originais extraídos da mensagem
        for(int i = 0; i < binarios_originais.length; i++){
           new_binario += binarios_originais[i];
        }
        // converte-os para inteiro.
        codigoAscii = Integer.parseInt(new_binario, 2);

        // por fim, devolve o caractere, por meio de um casting...
        this.mensagem += (char)codigoAscii;
    }
    
    private void decodificarDadoHemming(boolean[] bits){
        /*
        Método que recebe os dados de hamming, possivelmente, alterados.Se estiverem, corrige o erro para manter
        a integridade dos dados.
        :parametro: boolean[]
        :retorno: void
        */

        /*
         Identificação do erro pela operação XOR, com a mesma lógica contida no receptor, seguindo o método resolutivo
        tradicional de hamming.
         */
        boolean h1 = bits[0] ^ bits[2] ^ bits[4] ^ bits[6] ^ bits[8] ^ bits[10];
        boolean h2 = bits[1] ^ bits[2] ^ bits[5] ^ bits[6] ^ bits[9] ^ bits[10];
        boolean h3 = bits[3] ^ bits[4] ^ bits[5] ^ bits[6] ^ bits[11];
        boolean h4 = bits[7] ^ bits[8] ^ bits[9] ^ bits[10] ^ bits[11];

        /*
        Concatena strings, afim de determinar o resultado de hamming, encontrando, possivelmente, a posição binária
        de um erro.
        */
        String bit_formado = h4 + "" + h3 + "" + h2 + "" + h1;


        // troca o booleano para strings de modo a formar um número binário que será comparado com a posição do erro.
        String em_bits_numericos = bit_formado.replaceAll("false", "0").replaceAll("true", "1");

        int posicao_encontrada = -1;
        String bit;

        /*
        completa os bits para serem comparados depois, uma vez que o método Integer.toBinary não apresenta os bits
        com 4 dígitos.
        */
        if(!em_bits_numericos.equals("0000")){
            for(int i = 0; i < bits.length; i++){
                bit = Integer.toBinaryString(i);
                if(bit.length()==3){
                    bit = "0" + bit;
                }else{
                    if(bit.length() == 2){
                        bit = "00" + bit;
                    }
                    if(bit.length() == 1){
                        bit = "000" + bit;
                    }
                }

                // se for igual aos bits definidos acima, define a posição do erro e quebra o laço de repetição
                if(bit.equals(em_bits_numericos)){
                    System.out.println(CYAN_BOLD + "\nposição do erro: " + (i - 1));
                    posicao_encontrada = i - 1;  // armazena posição do erro
                    break;
                }
            }
        }


        /*
         Correção do erro com base na posição encontrada no loop acima.
         Para isso, se, de fato, houver erro (posição do erro for diferente de -1), ele inverterá a informação encontrada
         ,se não, manterá a informação.
         */
        if(posicao_encontrada != -1){
            bits[posicao_encontrada] = !bits[posicao_encontrada]; // inverte o valor que estiver na posição onde o erro foi encontrado
            System.out.println(GREEN_BOLD + "Informação corrigida: ");

            for(boolean itens : bits) System.out.print(itens + " ");
        }
        else{
            System.out.println(GREEN_BOLD + "\nInformação recebida sem ruídos ");
            for(boolean itens : bits) System.out.print(itens + " ");
        }
        decodificarDado(bits);
    }
    
    
    //recebe os dados do transmissor
    public void receberDadoBits(boolean[] bits){
        // impressão que exibe os dados recebidos pelo servidor, possivelmente, com ruídos.
        System.out.println(PURPLE_BOLD + "Dados recebidos pelo servidor: ");
        for(int i = 0; i < bits.length; i++) System.out.print(bits[i] + " ");

        //aqui você deve trocar o médodo decofificarDado para decoficarDadoHemming (implemente!!)
        decodificarDadoHemming(bits);
        System.out.println("\n");
    }
}
