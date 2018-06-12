package de.markus.statbot.ui;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.ChartConfig;
import com.byteowls.vaadin.chartjs.config.PieChartConfig;
import com.byteowls.vaadin.chartjs.data.Dataset;
import com.byteowls.vaadin.chartjs.data.PieDataset;
import com.byteowls.vaadin.chartjs.utils.ColorUtils;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.VaadinRequest;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.*;
import de.markus.statbot.model.User;
import de.markus.statbot.repositories.MessageRepository;
import de.markus.statbot.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@SpringUI
@Slf4j
public class StatBotUi extends UI {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    public void init(VaadinRequest vaadinRequest) {
        setContent(createLayout());
    }

    private Layout createLayout() {
        List<User> users = userRepository.findAll();
        String[] names = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            names[i] = users.get(i).getGlobalName();
        }
        PieChartConfig config = new PieChartConfig();
        config
                .data()
                .labels(names)
                .addDataset(new PieDataset().label("Dataset 1"))
                .and();

        config.
                options()
//                .maintainAspectRatio(true)
                .responsive(true)
                .title()
                .display(true)
                .text("Chart.js Pie Chart (Data Refresh)")
                .and()
                .animation()
                //.animateScale(true)
                .animateRotate(true)
                .and()
                .done();

        ChartJs chart = new ChartJs(config);
        chart.setJsLoggingEnabled(true);
        refreshChartData(chart, users);
        chart.setHeight("45%");
        chart.setWidth("45%");

        Button refreshButton = new Button("Refresh Data", VaadinIcons.REFRESH);
        refreshButton.addClickListener(e -> refreshChartData(chart, users));

        VerticalLayout layout = new VerticalLayout();
        layout.setSizeFull();
        layout.addComponent(refreshButton);
        layout.addComponent(chart);
        layout.setComponentAlignment(refreshButton, Alignment.TOP_CENTER);
        layout.setComponentAlignment(chart, Alignment.TOP_CENTER);
        layout.setExpandRatio(chart, 11);
        return layout;
    }

    private void refreshChartData(ChartJs chart, List<User> users) {
        generateData(chart.getConfig(), users);
        chart.refreshData();
    }

    private void generateData(ChartConfig chartConfig, List<User> users) {
        PieChartConfig config = (PieChartConfig) chartConfig;
        List<String> labels = config.data().getLabels();
        for (Dataset<?, ?> ds : config.data().getDatasets()) {
            PieDataset lds = (PieDataset) ds;
            List<Double> data = new ArrayList<>();
            List<String> colors = new ArrayList<>();
            for (int i = 0; i < labels.size(); i++) {
                data.add(messageRepository.countByAuthor(users.get(i)));
                colors.add(ColorUtils.randomColor(0.7));
            }
            lds.backgroundColor(colors.toArray(new String[colors.size()]));
            lds.dataAsList(data);
        }
    }
}

