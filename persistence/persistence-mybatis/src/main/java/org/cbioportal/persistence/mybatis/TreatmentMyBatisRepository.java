package org.cbioportal.persistence.mybatis;

import static java.util.stream.Collectors.groupingBy;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.cbioportal.model.ClinicalEventSample;
import org.cbioportal.model.Treatment;
import org.cbioportal.persistence.TreatmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TreatmentMyBatisRepository implements TreatmentRepository {
    @Autowired
    private TreatmentMapper treatmentMapper;
    
    @Override
    public Map<String, List<Treatment>> getTreatmentsByPatientId(List<String> sampleIds, List<String> studyIds, String key) {
        return treatmentMapper.getAllTreatments(sampleIds, studyIds, key)
            .stream()
            .collect(groupingBy(Treatment::getPatientId));
    }

    @Override
    public Map<String, List<ClinicalEventSample>> getSamplesByPatientId(List<String> sampleIds, List<String> studyIds) {
        return treatmentMapper.getAllSamples(sampleIds, studyIds)
            .stream()
            .sorted(Comparator.comparing(ClinicalEventSample::getTimeTaken)) // put earliest events first
            .distinct() // uniqueness determined by sample id, patient id, and study id
            // combined, the sort and distinct produce the earliest clinical event row for each unique sample
            .collect(groupingBy(ClinicalEventSample::getPatientId));
    }

    public Map<String, List<ClinicalEventSample>> getShallowSamplesByPatientId(List<String> sampleIds, List<String> studyIds) {
        return treatmentMapper.getAllShallowSamples(sampleIds, studyIds)
            .stream()
            .distinct()
            .collect(groupingBy(ClinicalEventSample::getPatientId));
    }

    @Override
    public Set<String> getAllUniqueTreatments(List<String> sampleIds, List<String> studyIds, String key) {
        return treatmentMapper.getAllUniqueTreatments(sampleIds, studyIds, key);
    }

    @Override
    public Integer getTreatmentCount(List<String> studies, String key) {
        return treatmentMapper.getTreatmentCount(null, studies, key);
    }

    @Override
    public Integer getSampleCount(List<String> studies) {
        return treatmentMapper.getSampleCount(null, studies);
    }
}
