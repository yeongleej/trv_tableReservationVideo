package com.ssafy.jariyo.domain.s3image.repository;

import com.ssafy.jariyo.domain.s3image.entity.S3Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface S3ImageRepository extends JpaRepository<S3Image, Long>  {

}
