package md.social.filemanager.repository;

import md.social.filemanager.model.FileData;
import org.hibernate.annotations.SQLDelete;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

@SQLDelete(sql = "UPDATE file_manager.file_data SET deleted = true WHERE id = ?")
public interface StorageRepository extends JpaRepository<FileData, Long>
{
    Page<FileData> findAllByCreatedBy(String userName, Pageable pageable);
}
