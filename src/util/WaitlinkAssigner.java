package util;

import go.builtin.channel.SudoG;

import java.util.function.Consumer;

public class WaitlinkAssigner implements Consumer<SudoG> {
    private final SudoG theG;
    public WaitlinkAssigner(SudoG sg) {
        theG = sg;
    }
    @Override
    public void accept(SudoG sudoG) {
        theG.waitlink = sudoG;
    }
}
