package org.example.Service;
import jakarta.transaction.SystemException;
import jakarta.transaction.Transactional;
import org.example.DTO.ResumeDTO;
import org.example.Mapper.ResumeMapper;
import org.example.Repository.ResumeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResumeService {
    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);
    @Autowired
    private ResumeRepository resumeRepository;
    @Autowired
    private ResumeMapper resumeMapper;
    @Autowired
    private NotificationService notificationService;

    @Transactional
    public void upload(Long userId, String title, String skills, String experience, String education){
        ResumeDTO resumeDTO = new ResumeDTO();
        resumeDTO.setTitle(title);
        resumeDTO.setSkills(skills);
        resumeDTO.setExperience(experience);
        resumeDTO.setEducation(education);
        resumeDTO.setUserId(userId);
        resumeRepository.save(resumeMapper.toEntity(resumeDTO));
        logger.info("Резюме успешно опубликовано.");
    }

    @Transactional
    public void update(Long id, ResumeDTO  resumeDTO) throws SystemException {
        resumeRepository.findById(id)
                .map(resume -> {
                    resume.setTitle(resumeDTO.getTitle());
                    resume.setSkills(resumeDTO.getSkills());
                    resume.setExperience(resumeDTO.getExperience());
                    resume.setEducation(resumeDTO.getEducation());
                    return resumeRepository.save(resume);
                })
                .orElseThrow(() -> new RuntimeException("Резюме не найдено"));
    }

    public void delete(Long id) throws SystemException {
        ResumeDTO resumeDTO = resumeMapper.toDTO(resumeRepository.findResumeById(id));
        if(resumeDTO != null){
            resumeRepository.deleteById(id);
            logger.info("Резюме успешно удалено.");
            notificationService.createAndSendNotification(resumeDTO.getUserId(), "Ваше резюме удалено.", null);
        }else
            logger.info("Резюме не найдено.");
    }

    public List<ResumeDTO> findResumesByUserId(Long id) throws SystemException {
        return resumeRepository.findByUserId(id)
                .stream().map(resume -> resumeMapper.toDTO(resume)).collect(Collectors.toList());
    }

    public ResumeDTO findResumeById(Long id) throws SystemException {
        return resumeMapper.toDTO(resumeRepository.findById(id).get());
    }

    public List<ResumeDTO> getAllResumes() throws SystemException {
        return resumeRepository.findAll()
                .stream().map(resumeMapper::toDTO).collect(Collectors.toList());
    }

    public List<ResumeDTO> findByTitleContainingIgnoreCase(String title) {
        return resumeRepository.findByTitleContainingIgnoreCase(title)
                .stream().map(resume -> resumeMapper.toDTO(resume)).collect(Collectors.toList());
    }

    public boolean userHasResume(Long userId) {
        return resumeRepository.existsByUserId(userId);
    }
}
