package fun.jevon.kaoyan.service;

import fun.jevon.kaoyan.entity.Subject;
import fun.jevon.kaoyan.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public List<Subject> listAll() {
        return subjectRepository.findAll();
    }
}
