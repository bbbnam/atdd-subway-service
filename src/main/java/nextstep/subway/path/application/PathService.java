package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.path.domain.SectionEdge;
import nextstep.subway.path.domain.SubwayMapData;
import nextstep.subway.path.domain.SubwayNavigation;
import nextstep.subway.path.dto.PathRequest;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.domain.Station;
import nextstep.subway.station.domain.StationRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class PathService {

    private LineRepository lineRepository;
    private StationRepository stationRepository;

    public PathService(LineRepository lineRepository, StationRepository stationRepository) {
        this.lineRepository = lineRepository;
        this.stationRepository = stationRepository;
    }

    public PathResponse findShortestPath(PathRequest pathRequest) {
        List<Line> lines = lineRepository.findAll();
        Station source = findStation(pathRequest.getSource());
        Station target = findStation(pathRequest.getTarget());
        SubwayNavigation subwayNavigation = new SubwayNavigation(new SubwayMapData(lines, SectionEdge.class).initData());

        return PathResponse.of(subwayNavigation.getPaths(source, target),
                subwayNavigation.getDistance(source, target));
    }

    private Station findStation(Long id) {
        return stationRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
}
