package com.example.backend.s3;

import com.example.backend.common.base.EntityDate;
import com.example.backend.common.enums.FileUsage;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "file_upload",
        indexes = {
                @Index(name = "idx_file_usage", columnList = "file_usage"),
                @Index(name = "idx_file_ref", columnList = "ref_type, ref_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_stored_file_name", columnNames = {"stored_file_name"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FileUpload extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * S3 Key 또는 저장 파일명
     */
    @Column(name = "stored_file_name", nullable = false, length = 255)
    private String storedFileName;

    /**
     * 원본 파일명
     */
    @Column(name = "original_file_name", nullable = false, length = 255)
    private String originalFileName;

    /**
     * 퍼블릭 접근 URL
     */
    @Column(nullable = false, length = 1000)
    private String url;

    /**
     * MIME 타입
     */
    @Column(name = "content_type", length = 120)
    private String contentType;

    /**
     * 파일 크기(Byte)
     */
    private Long size;

    /**
     * 어디에 쓰는 파일인지
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "file_usage", nullable = false, length = 20)
    private FileUsage usage;

    /**
     * 참조 정보
     */
    @Column(name = "ref_type", length = 50)
    private String refType;

    @Column(name = "ref_id")
    private Long refId;

    /**
     * 연관(참조) 정보 설정/변경
     */
    public void setReference(String refType, Long refId) {
        this.refType = refType;
        this.refId = refId;
    }

    /**
     * 메타정보 업데이트
     */
    public void updateMeta(String contentType, Long size) {
        this.contentType = contentType;
        this.size = size;
    }
}