package se.cfor.solarmonitor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by FORSLUNC on 2015-03-23.
 */
public class PlotFragment extends Fragment implements DataListener {
    private MySeries series1;
    private XYPlot plot;

    public PlotFragment()
    {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("PlotFragment->onDestroy");

    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("PlotFragment->onStart");
    }

    @Override
    public void onResume() {
        ((MyApplication)getActivity().getApplication()).addDataListener(this);
        super.onResume();
        System.out.println("PlotFragment->onResume");
    }

    @Override
    public void onPause() {
        ((MyApplication)getActivity().getApplication()).removeDataListener(this);
        super.onPause();
        System.out.println("PlotFragment->onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("PlotFragment->onStop");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        System.out.println("PlotFragment->onCreate");
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.plotframe,container,false);
        System.out.println("PlotFragment->onCreateView");

        Button sweepButton = (Button)view.findViewById(R.id.sweepButton);
        sweepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MyApplication)getActivity().getApplication()).writeToSocket("AT+SWEEP\r");
            }
        });

        // initialize our XYPlot reference:
        plot = (XYPlot) view.findViewById(R.id.mySimpleXYPlot);
        // Create a couple arrays of y-values to plot:
        Number[] x = {0,3.3};
        Number[] y = {0,3};

        // Turn the above arrays into XYSeries':
        series1 = new MySeries(Arrays.asList(x),Arrays.asList(y), "voltage");

        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        //series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(view.getContext(),
                R.xml.line_point_formatter_with_plf1);

        series1Format.getVertexPaint().setColor(Color.TRANSPARENT);
        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);


        plot.getLayoutManager().remove(plot.getLegendWidget());
        plot.getLayoutManager().remove(plot.getDomainLabelWidget());
        plot.getLayoutManager().remove(plot.getRangeLabelWidget());
        plot.getLayoutManager().remove(plot.getTitleWidget());
        plot.getGraphWidget().getBackgroundPaint().setColor(Color.WHITE);
        plot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);


        plot.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

        plot.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
        plot.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);
        plot.getGraphWidget().getRangeOriginLinePaint().setColor(Color.BLACK);
        plot.setBorderStyle(XYPlot.BorderStyle.NONE, null, null);

        plot.setPlotMargins(0,0,0,0);
        plot.setPlotPadding(0,0,0,0);
        plot.getGraphWidget().setPadding(30,25,10,45);
        plot.getGraphWidget().setSize(new SizeMetrics(
                0, SizeLayoutType.FILL,
                0, SizeLayoutType.FILL));

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
        plot.getGraphWidget().setDomainLabelOrientation(-45);


        return view;
    }

    @Override
    public void onNewData(String data) {
        String[] rows = data.split("\\n");
        String[] strValues;
        this.series1.clearData();
        for (String thisRow:rows) {
            strValues = thisRow.split(" ");
            if (strValues.length == 2) {
                this.series1.addPoint(Float.parseFloat(strValues[0]), Float.parseFloat(strValues[1]));
            }
        }
        plot.redraw();
    }

    private void redraw() {
    }
    class MySeries implements XYSeries {
        private List<Number> xData;
        private List<Number> yData;
        private String title;

        public MySeries(List<Number> xData, List<Number> yData, String title) {
            this.xData = xData;
            this.yData = yData;
            this.title = title;
        }
        public void clearData() {
            this.xData = new ArrayList<Number>();
            this.yData = new ArrayList<Number>();
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public int size() {
            return xData.size();
        }

        @Override
        public Number getX(int index) {
            return xData.get(index);
        }

        @Override
        public Number getY(int index) {
            return yData.get(index);
        }
        public void addPoint(float x, float y) {
            xData.add(x);
            yData.add(y);
        }
    }

}
