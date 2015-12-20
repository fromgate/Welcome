package ru.nukkit.welcome.util;

import cn.nukkit.metadata.MetadataValue;
import ru.nukkit.welcome.Welcome;

public class LoginMeta extends MetadataValue {
    public LoginMeta() {
        super(Welcome.getPlugin());
    }

    @Override
    public Object value() {
        return true;
    }

    @Override
    public void invalidate() {
    }
}
