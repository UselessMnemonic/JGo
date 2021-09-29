package util;

import go.builtin.channel.SudoG;
import go.runtime.G;

import java.util.function.Consumer;

public class WaitingAssigner implements Consumer<SudoG> {
    private final G theG;
    public WaitingAssigner(G g) {
        theG = g;
    }
    @Override
    public void accept(SudoG sudoG) {
        theG.waiting = sudoG;
    }
}
