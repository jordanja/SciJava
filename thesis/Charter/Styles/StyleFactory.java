package thesis.Charter.Styles;

public class StyleFactory {
	public static Style getStyle(Styles style) {
		if (style == Styles.Matplotlib) {
			return new MatplotlibStyle();
		} else if (style == Styles.Seaborn) {
			return new SeabornStyle();
		} else if (style == Styles.Excel) {
			return new ExcelStyle();
		} else if (style == Styles.ChartJS) {
			return new ChartJSStyle();
		} else if (style == Styles.Nighttime) {
			return new NighttimeStyle();
		} else if (style == Styles.Kids) {
			return new KidsStyle();
		}
		return null;
	}
}
