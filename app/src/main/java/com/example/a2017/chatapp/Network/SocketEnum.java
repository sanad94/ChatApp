package com.example.a2017.chatapp.Network;

public enum  SocketEnum
{
    TYPING("TYPING_SOCKET")
    ,ONLINE("ONLINE_SOCKET");

    private final String text;

    /**
     * @param text
     */
    SocketEnum(final String text) {
        this.text = text;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
}
