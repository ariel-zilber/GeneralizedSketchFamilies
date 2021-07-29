import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestUtils {
    // https://github.com/sh0nk/matplotlib4j
    public static void main(String [] args){
        Plot plt = Plot.create();
        plt.plot()
                .add(Arrays.asList(1.3, 2))
                .label("label")
                .linestyle("--");
        plt.xlabel("xlabel");
        plt.ylabel("ylabel");
        plt.text(0.5, 0.2, "text");
        plt.title("Title!");
        plt.legend();
        plt.show();
        List<Double> x = NumpyUtils.linspace(-Math.PI, Math.PI, 256);
        List<Double> C = x.stream().map(xi -> Math.cos(xi)).collect(Collectors.toList());
        List<Double> S = x.stream().map(xi -> Math.sin(xi)).collect(Collectors.toList());

    }
}
