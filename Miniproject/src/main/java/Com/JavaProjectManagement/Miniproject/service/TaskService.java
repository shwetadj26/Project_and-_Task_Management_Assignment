package Com.JavaProjectManagement.Miniproject.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Com.JavaProjectManagement.Miniproject.dto.TaskDTO;
import Com.JavaProjectManagement.Miniproject.entity.Project;
import Com.JavaProjectManagement.Miniproject.entity.Task;
import Com.JavaProjectManagement.Miniproject.entity.Task.TaskPriority;
import Com.JavaProjectManagement.Miniproject.entity.Task.TaskStatus;
import Com.JavaProjectManagement.Miniproject.entity.User;
import Com.JavaProjectManagement.Miniproject.exception.ResourceNotFoundException;
import Com.JavaProjectManagement.Miniproject.exception.UnauthorizedException;
//import Com.JavaProjectManagement.Miniproject.model.TaskPriority;
//import Com.JavaProjectManagement.Miniproject.model.TaskStatus;
import Com.JavaProjectManagement.Miniproject.repository.ProjectRepo;
import Com.JavaProjectManagement.Miniproject.repository.TaskRepo;
import Com.JavaProjectManagement.Miniproject.repository.UserRepo;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TaskService {

    @Autowired
    private TaskRepo taskRepository;

    @Autowired
    private ProjectRepo projectRepository;

    @Autowired
    private UserRepo userRepository;

    public TaskDTO createTask(Long projectId, TaskDTO taskDTO) {
        User user = getCurrentUser();
        log.info("Creating new task for project: {} by user: {}",
projectId, user.getUsername());

        Project project =
projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + projectId));

        Task task = new Task();
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(taskDTO.getStatus() != null ? taskDTO.getStatus() : TaskStatus.PENDING);
        task.setPriority(taskDTO.getPriority() != null ? taskDTO.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(taskDTO.getDueDate());
        task.setProject(project);

        Task savedTask = taskRepository.save(task);
        log.info("Task created successfully with ID: {}", savedTask.getId());

        return mapTaskToDTO(savedTask);
    }

    public List<TaskDTO> getTasksByProjectId(Long projectId) {
        User user = getCurrentUser();
        log.info("Fetching tasks for project: {} by user: {}",
projectId, user.getUsername());

        Project project =
projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new
ResourceNotFoundException("Project not found with ID: " + projectId));

        List<Task> tasks = taskRepository.findByProjectId(projectId);
        return tasks.stream().map(this::mapTaskToDTO).collect(Collectors.toList());
    }

    public TaskDTO getTaskById(Long projectId, Long taskId) {
        User user = getCurrentUser();
        log.info("Fetching task: {} for project: {} by user: {}",
taskId, projectId, user.getUsername());

        Project project =
projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new
ResourceNotFoundException("Project not found with ID: " + projectId));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        return mapTaskToDTO(task);
    }

    public TaskDTO updateTask(Long projectId, Long taskId, TaskDTO taskDTO) {
        User user = getCurrentUser();
        log.info("Updating task: {} for project: {} by user: {}",
taskId, projectId, user.getUsername());

        Project project =
projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new
ResourceNotFoundException("Project not found with ID: " + projectId));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        if (taskDTO.getTitle() != null && !taskDTO.getTitle().isBlank()) {
            task.setTitle(taskDTO.getTitle());
        }
        if (taskDTO.getDescription() != null) {
            task.setDescription(taskDTO.getDescription());
        }
        if (taskDTO.getStatus() != null) {
            task.setStatus(taskDTO.getStatus());
        }
        if (taskDTO.getPriority() != null) {
            task.setPriority(taskDTO.getPriority());
        }
        if (taskDTO.getDueDate() != null) {
            task.setDueDate(taskDTO.getDueDate());
        }

        Task updatedTask = taskRepository.save(task);
        log.info("Task updated successfully: {}", taskId);

        return mapTaskToDTO(updatedTask);
    }

    public void deleteTask(Long projectId, Long taskId) {
        User user = getCurrentUser();
        log.info("Deleting task: {} for project: {} by user: {}",
taskId, projectId, user.getUsername());

        Project project =
projectRepository.findByIdAndUserId(projectId, user.getId())
                .orElseThrow(() -> new
ResourceNotFoundException("Project not found with ID: " + projectId));

        Task task = taskRepository.findByIdAndProjectId(taskId, projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        taskRepository.delete(task);
        log.info("Task deleted successfully: {}", taskId);
    }

    public List<TaskDTO> searchTasks(String query) {
        User user = getCurrentUser();
        log.info("Searching tasks for user: {} with query: {}",
user.getUsername(), query);

        List<Task> tasks =
taskRepository.searchTasksByUserIdAndQuery(user.getId(), query);
        return tasks.stream().map(this::mapTaskToDTO).collect(Collectors.toList());
    }

    public List<TaskDTO> filterAndSortTasks(TaskStatus status,
TaskPriority priority, String sortBy, String order) {
        User user = getCurrentUser();
        log.info("Filtering tasks for user: {} with status: {},priority: {}, sortBy: {}, order: {}",
                user.getUsername(), status, priority, sortBy, order);

        List<Task> tasks;

        if (status != null && priority != null) {
            tasks =
taskRepository.findTasksByStatusAndPriorityForUser(user.getId(),
status, priority);
        } else if ("priority".equalsIgnoreCase(sortBy)) {
            tasks =
taskRepository.findAllTasksByUserIdOrderByPriority(user.getId());
        } else {
            tasks =
taskRepository.findAllTasksByUserIdOrderByDueDate(user.getId());
        }

        if ("desc".equalsIgnoreCase(order)) {
            tasks = tasks.stream().sorted((t1, t2) -> {
                if ("priority".equalsIgnoreCase(sortBy)) {
                    return t2.getPriority().compareTo(t1.getPriority());
                } else {
                    return t2.getDueDate() != null && t1.getDueDate() != null ?
                            t2.getDueDate().compareTo(t1.getDueDate()) : 0;
                }
            }).collect(Collectors.toList());
        }

        return tasks.stream().map(this::mapTaskToDTO).collect(Collectors.toList());
    }

    private TaskDTO mapTaskToDTO(Task task) {
        return TaskDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .projectId(task.getProject().getId())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
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