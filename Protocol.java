interface CMD {
    static final byte CMD_CONNECT     = 1;  // Подключение к чату
    static final byte CMD_DISCONNECT  = 2;  // Отключение от чата
    static final byte CMD_MESSAGE     = 3;  // Отправка публичного сообщения
    static final byte CMD_USER        = 4;  // Запрос списка пользователей
    static final byte CMD_BROADCAST   = 5;  // Рассылка сообщения всем
}
interface RESULT {
    static final int RESULT_CODE_OK     = 0;
    static final int RESULT_CODE_ERROR  = -1;
}
interface PORT {
    static final int PORT = 8071;
}
public class Protocol implements CMD, RESULT, PORT {
    private static final byte CMD_MIN = CMD_CONNECT;
    private static final byte CMD_MAX = CMD_BROADCAST;

    public static boolean validID(byte id) {
        return id >= CMD_MIN && id <= CMD_MAX;
    }
}