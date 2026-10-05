ALTER TABLE `member`
    ADD COLUMN `onboard_yn` bit(1) NOT NULL DEFAULT b'0' AFTER `profile_image_url`;
