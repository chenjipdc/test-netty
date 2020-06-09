package top.chenjipdc.testnetty.bio;

/**
 * @author chenjipdc@gmail.com
 * @date 2020-06-04 17:50
 */
public class BioClientMain {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            BioUtils.clientSend(i);
        }
    }
}
