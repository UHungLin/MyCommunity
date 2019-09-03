package fun.linyuhong.myCommunity.util;


import org.springframework.context.annotation.Configuration;


public class XORUtil {

    /**
     * 异或算法加密/解密
     *
     * @param id 数据（密文/明文）
     * @param key 密钥
     * @return 返回解密/加密后的数据
     */
    public static int encryptId(int id, byte[] key) {
        byte[] ids = leIntToByteArray(id);
        if (ids == null || ids.length == 0 || key == null || key.length == 0) {
            return id;
        }

        byte[] result = new byte[ids.length];

        // 使用密钥字节数组循环加密或解密
        for (int i = 0; i < ids.length; i++) {
            // 数据与密钥异或, 再与循环变量的低8位异或（增加复杂度）
            result[i] = (byte) (ids[i] ^ key[i % key.length] ^ (i & 0xFF));
        }

        return byteArrayToLeInt(result);
    }

    /**
     * 将 int 转化为 byte[]
     * @param value
     * @return
     */
    private static byte[] leIntToByteArray(int value) {
        byte[] encodedValue = new byte[Integer.SIZE / Byte.SIZE];
        encodedValue[3] = (byte) (value >> Byte.SIZE * 3);
        encodedValue[2] = (byte) (value >> Byte.SIZE * 2);
        encodedValue[1] = (byte) (value >> Byte.SIZE);
        encodedValue[0] = (byte) value;
        return encodedValue;
    }

    private static int byteArrayToLeInt(byte[] encodedValue) {
        int value = (encodedValue[3] << (Byte.SIZE * 3));
        value |= (encodedValue[2] & 0xFF) << (Byte.SIZE * 2);
        value |= (encodedValue[1] & 0xFF) << (Byte.SIZE * 1);
        value |= (encodedValue[0] & 0xFF);
        return value;
    }

}
