package core.scene.stage.actor.widget.chart;

public class ChartData {
	
	public Double[][] values;
	
	public String[] categories;
	
	public String[] series;

	/*package*/ void validate() {
		if(values == null) values = new Double[0][0];
		if(categories == null) categories = new String[0];
		if(series == null) series = new String[0];
		
	}
}