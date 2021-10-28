package io.jenkins.plugins.forensics.miner;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.echarts.Build;
import edu.hm.hafner.echarts.BuildResult;
import edu.hm.hafner.echarts.ChartModelConfiguration;
import edu.hm.hafner.echarts.LinesChartModel;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests CodeMetricTrendChart.
 *
 * @author Nikolas Paripovic
 *
 */
public class CodeMetricTrendChartTest {

    private final CodeMetricTrendChart codeMetricTrendChart = new CodeMetricTrendChart();

    @Test
    void shouldCreate() {
        Iterable<BuildResult<ForensicsBuildAction>> buildResults = createBuildResults(false);
        ChartModelConfiguration chartModelConfiguration = createChartModelConfiguration();

        LinesChartModel linesChartModel = codeMetricTrendChart.create(buildResults, chartModelConfiguration);

        assertThat(linesChartModel.getSeries()).isEmpty();
        assertThat(linesChartModel.getBuildNumbers()).isEmpty();
        assertThat(linesChartModel.getDomainAxisLabels()).isEmpty();
        assertThat(linesChartModel.getDomainAxisLabels()).isEmpty();
    }

    @Test
    void shouldCreateWithData() {

        Iterable<BuildResult<ForensicsBuildAction>> buildResults = createBuildResults(true);
        ChartModelConfiguration chartModelConfiguration = createChartModelConfiguration();

        LinesChartModel linesChartModel = codeMetricTrendChart.create(buildResults, chartModelConfiguration);

        linesChartModel.getSeries().forEach(ser -> System.out.println("Data: " + ser.getData() + ", name: " + ser.getName()));

        assertThat(linesChartModel.getSeries()).hasSize(2);
        assertThat(linesChartModel.getSeries()).allSatisfy(series -> assertThat(series.getData()).hasSize(4));
        assertThat(linesChartModel.getBuildNumbers()).hasSize(4);
        assertThat(linesChartModel.getBuildNumbers()).containsExactly(1, 4, 7, 10);
    }

    private Iterable<BuildResult<ForensicsBuildAction>> createBuildResults(final boolean withData) {
        List<BuildResult<ForensicsBuildAction>> buildResults = new ArrayList<>();
        if (withData) {
            buildResults.add(createResult(1));
            buildResults.add(createResult(4));
            buildResults.add(createResult(7));
            buildResults.add(createResult(10));
        }
        return buildResults;
    }

    private BuildResult<ForensicsBuildAction> createResult(final int buildNumber) {
        ForensicsBuildAction action = mock(ForensicsBuildAction.class);
        Build build = new Build(buildNumber);
        return new BuildResult<>(build, action);
    }

    private ChartModelConfiguration createChartModelConfiguration() {
        return new ChartModelConfiguration();
    }


}
