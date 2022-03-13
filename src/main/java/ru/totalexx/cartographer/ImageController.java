package ru.totalexx.cartographer;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping(value = "/chartas")
public class ImageController {

    @PostMapping("/")
    public ResponseEntity createImage(@RequestParam int width,
                                      @RequestParam int height) {
        String imageID = "";
        try {
            imageID = ImageModel.createImage(width, height);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(imageID);
    }

    @PostMapping("/{id}/")
    public ResponseEntity saveFragment(@PathVariable String id,
                                       @RequestParam int x,
                                       @RequestParam int y,
                                       @RequestParam int width,
                                       @RequestParam int height
    ) {
        ImageModel.saveImageFragment(id, x, y, width, height);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @GetMapping("/{id}/")
    public ResponseEntity getFragment(@PathVariable String id,
                              @RequestParam int x,
                              @RequestParam int y,
                              @RequestParam int width,
                              @RequestParam int height
    ) {
        ImageModel.getImageFragment();
    }

    @DeleteMapping("/{id}/")
    public ResponseEntity deleteImage(@PathVariable String id) {
        if (ImageModel.deleteImage(id)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(400).build();
    }

}
