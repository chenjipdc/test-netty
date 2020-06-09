package top.chenjipdc.testnetty.nio;

/**
 * @author chenjipdc@gmail.com
 * @date 2020-06-04 17:50
 */
public class NioClientMain {
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            NioUtils.startSyncClient(i);
        }
    }
}
