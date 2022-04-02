package transferenciadadostp;

public class Receptor {
    public static final String BLACK_BOLD = "\033[1;30m";  // BLACK
    public static final String RED_BOLD = "\033[1;31m";    // RED
    public static final String GREEN_BOLD = "\033[1;32m";  // GREEN
    public static final String YELLOW_BOLD = "\033[1;33m"; // YELLOW
    public static final String BLUE_BOLD = "\033[1;34m";   // BLUE
    public static final String PURPLE_BOLD = "\033[1;35m"; // PURPLE
    public static final String CYAN_BOLD = "\033[1;36m";   // CYAN
    public static final String WHITE_BOLD = "\033[1;37m";  // WHITE

    //mensagem recebida pelo transmissor
    private String mensagem;

    public Receptor() {
        //mensagem vazia no início da execução
        this.mensagem = "";
    }
    
    public String getMensagem() {
        return mensagem;
    }
 
    private void decodificarDado(boolean bits[]){
        int codigoAscii = 0;
        int expoente = bits.length-1;

        int[] bits_ = new int[bits.length];
        int count = 0;
        for(boolean bit_ : bits){
            if(bit_){
                bits_[count] = 1;
            }else{
                bits_[count] = 0;
            }
            count ++;
        }
        // binario original: 1byte

        int[] binarios_originais = {bits_[2],bits_[4], bits_[5], bits_[6], bits_[8], bits_[9], bits_[10], bits_[11]};

        String new_binario = "";
        for(int i = 0; i < binarios_originais.length; i++){
           new_binario += binarios_originais[i];
        }
        codigoAscii = Integer.parseInt(new_binario, 2);

        this.mensagem += (char)codigoAscii;
    }
    
    private void decodificarDadoHemming(boolean[] bits){
        //implemente a decodificação Hemming  aqui e encontre os
        //erros e faça as devidas correções para ter a imagem correta

        // identificação de erro pela operação XOR
        boolean h1 = bits[0] ^ bits[2] ^ bits[4] ^ bits[6] ^ bits[8] ^ bits[10];
        boolean h2 = bits[1] ^ bits[2] ^ bits[5] ^ bits[6] ^ bits[9] ^ bits[10];
        boolean h3 = bits[3] ^ bits[4] ^ bits[5] ^ bits[6] ^ bits[11];
        boolean h4 = bits[7] ^ bits[8] ^ bits[9] ^ bits[10] ^ bits[11];

        String bit_formado = h4 + "" + h3 + "" + h2 + "" + h1;
        String em_bits_numericos = bit_formado.replaceAll("false", "0").replaceAll("true", "1");

        int posicao_encontrada = -1;
        String bit;

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

                if(bit.equals(em_bits_numericos)){
                    System.out.println(CYAN_BOLD + "\nposicao do erro: " + (i - 1));
                    posicao_encontrada = i - 1;  // armazena posição do erro
                    break;
                }
            }
        }


        // correção do erro:
        if(posicao_encontrada != -1){
            bits[posicao_encontrada] = !bits[posicao_encontrada]; // nega o valor que estiver na posição onde o erro foi encontrado
            // System.out.println("LOCALIZADO: " + bits[posicao_encontrada]); # debug
            System.out.println(GREEN_BOLD + "Informação corrigida: ");
            for(boolean itens : bits){
                System.out.print(itens + " ");
            }
        }else{
            System.out.println(GREEN_BOLD + "\nInformação recebida corretamente: ");
            for(boolean itens : bits){
                System.out.print(itens + " ");
            }
        }
        decodificarDado(bits);
    }
    
    
    //recebe os dados do transmissor
    public void receberDadoBits(boolean[] bits){
        System.out.println(PURPLE_BOLD + "Dados recebidos pelo servidor: ");
        for(int i = 0; i < bits.length; i++) System.out.print(bits[i] + " ");
        //aqui você deve trocar o médodo decofificarDado para decoficarDadoHemming (implemente!!)
        decodificarDadoHemming(bits);
        System.out.println("\n");
    }
}
