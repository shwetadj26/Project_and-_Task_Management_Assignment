package Com.JavaProjectManagement.Miniproject.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Com.JavaProjectManagement.Miniproject.entity.Project;
import Com.JavaProjectManagement.Miniproject.entity.User;

@Repository
public interface ProjectRepo extends JpaRepository<Project,Long>  
{
	List<Project> findByUserId(Long userId);

    Optional<Project> findByIdAndUserId(Long id, Long userId);

}
