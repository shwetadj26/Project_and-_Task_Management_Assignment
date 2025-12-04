package Com.JavaProjectManagement.Miniproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import Com.JavaProjectManagement.Miniproject.entity.Task;
import Com.JavaProjectManagement.Miniproject.entity.Task.TaskPriority;
import Com.JavaProjectManagement.Miniproject.entity.Task.TaskStatus;
import Com.JavaProjectManagement.Miniproject.entity.User;
//import Com.JavaProjectManagement.Miniproject.model.TaskPriority;
//import Com.JavaProjectManagement.Miniproject.model.TaskStatus;

public interface TaskRepo extends JpaRepository<Task,Long> 
{
	List<Task> findByProjectId(Long projectId);

    Optional<Task> findByIdAndProjectId(Long id, Long projectId);

    @Query("SELECT t FROM Task t WHERE t.project.user.id = :userId AND (LOWER(t.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(t.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Task> searchTasksByUserIdAndQuery(@Param("userId") Long userId, @Param("query") String query);

    @Query("SELECT t FROM Task t WHERE t.project.user.id = :userId AND t.status = :status AND t.priority = :priority ORDER BY t.dueDate ASC")
    List<Task> findTasksByStatusAndPriorityForUser(@Param("userId") Long userId, @Param("status") TaskStatus status, @Param("priority") TaskPriority priority);

    @Query("SELECT t FROM Task t WHERE t.project.user.id = :userId ORDER BY t.dueDate ASC")
    List<Task> findAllTasksByUserIdOrderByDueDate(@Param("userId") Long userId);

    @Query("SELECT t FROM Task t WHERE t.project.user.id = :userId ORDER BY t.priority DESC")
    List<Task> findAllTasksByUserIdOrderByPriority(@Param("userId") Long userId);

}


