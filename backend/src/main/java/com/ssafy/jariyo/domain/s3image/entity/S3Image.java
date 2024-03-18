package com.ssafy.jariyo.domain.s3image.entity;

import com.ssafy.jariyo.global.entity.BaseObject;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "s3image")
public class S3Image extends BaseObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "s3image_id", nullable = false)
    private Long s3imageId;

    @Column(name = "fileUrl", length = 255)
    private String fileUrl;

    @Column(name = "file_name", length = 255)
    private String fileName;

    public S3Image(String fileUrl, String fileName) {
        this.fileUrl = fileUrl;
        this.fileName = fileName;
    }
}
