package Com.JavaProjectManagement.Miniproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Com.JavaProjectManagement.Miniproject.dto.ProjectDTO;
import Com.JavaProjectManagement.Miniproject.entity.Project;
import Com.JavaProjectManagement.Miniproject.entity.User;
import Com.JavaProjectManagement.Miniproject.exception.ResourceNotFoundException;
import Com.JavaProjectManagement.Miniproject.exception.UnauthorizedException;
import Com.JavaProjectManagement.Miniproject.repository.ProjectRepo;
import Com.JavaProjectManagement.Miniproject.repository.UserRepo;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class ProjectService {

    @Autowired
    private ProjectRepo projectRepository;

    @Autowired
    private UserRepo userRepository;

    public ProjectDTO createProject(ProjectDTO projectDTO) {
        log.info("Creating new project: {}", projectDTO.getName());

        User user = getCurrentUser();
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDescription(projectDTO.getDescription());
        project.setUser(user);

        Project savedProject = projectRepository.save(project);
        log.info("Project created successfully with ID: {}",
savedProject.getId());

        return mapProjectToDTO(savedProject);
    }

    public List<ProjectDTO> getAllProjects() {
        User user = getCurrentUser();
        log.info("Fetching all projects for user: {}", user.getUsername());

        List<Project> projects = projectRepository.findByUserId(user.getId());
        return projects.stream().map(this::mapProjectToDTO).collect(Collectors.toList());
    }

    public ProjectDTO getProjectById(Long projectId) {
        User user = getCurrentUser();
        log.info("Fetching project: {} for user: {}", projectId,
user.getUsername());

        Project project =
projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        return mapProjectToDTO(project);
    }

    public ProjectDTO updateProject(Long projectId, ProjectDTO projectDTO) {
        User user = getCurrentUser();
        log.info("Updating project: {} for user: {}", projectId,
user.getUsername());

        Project project =
projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        if (projectDTO.getName() != null && !projectDTO.getName().isBlank()) {
            project.setName(projectDTO.getName());
        }
        if (projectDTO.getDescription() != null) {
            project.setDescription(projectDTO.getDescription());
        }

        Project updatedProject = projectRepository.save(project);
        log.info("Project updated successfully: {}", projectId);

        return mapProjectToDTO(updatedProject);
    }

    public void deleteProject(Long projectId) {
        User user = getCurrentUser();
        log.info("Deleting project: {} for user: {}", projectId,
user.getUsername());

        Project project =
projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        projectRepository.delete(project);
        log.info("Project deleted successfully: {}", projectId);
    }

    private ProjectDTO mapProjectToDTO(Project project) {
        return ProjectDTO.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .userId(project.getUser().getId())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }

    private User getCurrentUser() {
        Authentication authentication =
SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedException("User not found"));
    }

}
