package com.heima.utils.common;

import java.util.Random;

public class RvGenerator {
    public static double generatePowerLawRandom(double alpha, double min, double max) {
        Random random = new Random();
        double u = random.nextDouble(); // 生成 (0,1) 之间的随机数
        double exponent = -1.0 / (alpha - 1.0);
        return Math.pow((Math.pow(max, alpha - 1) - Math.pow(min, alpha - 1)) * u + Math.pow(min, alpha - 1), exponent);
    }

    public static double generateLogNormalRandom(double mean, double stdDev) {
        Random random = new Random();
        return Math.exp(mean + stdDev * random.nextGaussian());
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            int views = (int) generateLogNormalRandom(6.0, 3.5);
            int likes = generateLikes(views);
            int favorites = generateFavorites(views);
            System.out.println("观看量: " + views + "，点赞: " + likes + "，收藏: " + favorites);
        }
    }

    /**
     * 生成点赞数
     * @param views
     * @return
     */
    private static int generateLikes(int views) {
        Random random = new Random();
        double likeRate = 0.01 + (random.nextDouble() * 0.04); // 1% - 5% 观看量变为点赞
        return (int) (views * likeRate);
    }

    /**
     * 生成收藏数
     * @param views
     * @return
     */
    private static int generateFavorites(int views) {
        Random random = new Random();
        double favoriteRate = 0.005 + (random.nextDouble() * 0.02); // 0.5% - 2% 观看量变为收藏
        return (int) (views * favoriteRate);
    }

}
