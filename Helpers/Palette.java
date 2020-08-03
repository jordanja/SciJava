package thesis.Helpers;

import java.awt.Color;

public class Palette {
	public final static Color[] MorningMist = new Color[] {Color.decode("#084C61"), Color.decode("#82AEB1"), Color.decode("#DB504A"), Color.decode("#E3B505"), Color.decode("#4F6D7A")};
	public final static Color[] Mystery = new Color[] {Color.decode("#34344A"), Color.decode("#80475E"), Color.decode("#CC5A71"), Color.decode("#C89B7B"), Color.decode("#F0F757")};
	public final static Color[] Seventies = new Color[] {Color.decode("#EDE185"), Color.decode("#2CB282"), Color.decode("#0A777A"), Color.decode("#4E3964"), Color.decode("#521963")};
	public final static Color[] SnowBoard = new Color[] {Color.decode("#1C110A"), Color.decode("#E4D6A7"), Color.decode("#E9B44C"), Color.decode("#9B2915"), Color.decode("#50A2A7")};
	public final static Color[] Fire = new Color[] {Color.decode("#BC3908"), Color.decode("#941B0C"), Color.decode("#F6AA1C"), Color.decode("#220901"), Color.decode("#621708")};
	public final static Color[] Contrast = new Color[] {Color.decode("#c80003"), Color.decode("#a6c64c"), Color.decode("#ff955f"), Color.decode("#405d3a"), Color.decode("#86c6be")};

	public final static Color[] Default = new Color[] {new Color(60, 92, 160), new Color(211, 112, 65), new Color(70, 155, 85), new Color(181, 57, 65), new Color(109, 92, 163), new Color(128, 101, 78), new Color(208, 116, 182), new Color(121, 121, 121), new Color(192, 172, 97), new Color(84, 166, 194)};
	public final static Color[] Matplotlib = new Color[] {
		Color.decode("#1F77B4"),
		Color.decode("#FF7F0E"),
		Color.decode("#2CA02C"),
		Color.decode("#D62728"),
		Color.decode("#9467BD"),
		Color.decode("#8C564B"),
		Color.decode("#E377C2"),
		Color.decode("#7F7F7F"),
		Color.decode("#BCBD22"),
		Color.decode("#17BECF"),
	};
	
	public final static Color[] Excel = Default;

	public final static Color[] ChartJS = new Color[] {
		new Color(252, 72, 113),
		new Color(45, 142, 229),
		new Color(254, 196, 69),
		new Color(64, 181, 178),
		new Color(134, 71, 254),
		new Color(189, 191, 196)
	};
	
	public final static Color[] Neon = new Color[] {
		new Color(69, 237, 228),
		new Color(103, 240, 19),
		new Color(254, 230, 9),
		new Color(233, 0, 255),
		new Color(0, 0, 255),
		new Color(242, 0, 10),
		new Color(241, 131, 17),
	};
	
	public final static Color[] InfoGram = new Color[] {
		new Color(80, 148, 208),
		new Color(234, 102, 161),
		new Color(245, 148, 55),
		new Color(85, 179, 89),
		new Color(161, 97, 162),
		new Color(135, 135, 135),
		new Color(36, 97, 155),
		new Color(180, 119, 59),
		new Color(176, 106, 139),
		new Color(38, 144, 101),
		new Color(88, 86, 86),
		};
	
	public static Color[] generateUniqueColors(int numColors) {
		Color[] colors = new Color[numColors];
		
		for (int colorCount = 0; colorCount < numColors; colorCount++) {
			colors[colorCount] = Color.getHSBColor((float)colorCount / (float)numColors, 1, 1);
			
		}
		
		return colors;
	}
	
}
