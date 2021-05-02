clc
clear
%% evaluation on Test Set
addpath('metrics')
video_name = {'[01] KITTI - City','[02] KITTI - Person','[03] KITTI - Campus','[04] VIRAT Court','[05] VIRAT Student Campus','[06] VIRAT Full Parking Lot','[07] Wide Area','[08] Human Interaction','[09] Edinburgh Office','[10] MMDA Day','[11] Archer_s Eye','[12] Pasay Bike Incident','[13] Bus','[14] Convenience Store','[15] Retail Store','[16] Grocery Theft','[17] Abbey Road','[18] Wolves Highway','[19] Restaurant','[20] Halloween'};
%video_name = {'[20] Halloween',};
%video_name = {'[01] KITTI - City','[02] KITTI - Person', '[03] KITTI - Campus'}
psnr_set = [];
ssim_set = [];
rmse_set = [];
for idx_video = 1:length(video_name)
    psnr_video = [];
    ssim_video = [];
    rmse_video = [];
    name = char(video_name(idx_video))
    video_path = fullfile('app/IO', name)
    a=dir([video_path '/*.png'])
    disp(video_path)
    n=numel(a)-1
    disp(n)
    for idx_frame = 9:n 				% exclude the first and last 2 frames
        img_hr = imread(['E:/Projects/Thesis/Baseline B/SOF-VSR/TIP/data/test/Set/',video_name{idx_video},'/hr/hr_', num2str(idx_frame-9,'%d'),'.png']);
        img_sr = imread(['app/IO/',video_name{idx_video},'/HR_', num2str(idx_frame-9,'%d'),'.png']);
        
        sharpest_value = 0;
        cnt = 0;
        sharpest_image = img_hr;
        for i = idx_frame - 9: idx_frame
            temp = imread(['E:/Projects/Thesis/Baseline B/SOF-VSR/TIP/data/test/Set/',video_name{idx_video},'/hr/hr_', num2str(i,'%d'),'.png']);
            G = double(rgb2gray(temp));
            % measure the sharpness of image
            sharpness=estimate_sharpness(G);
            if sharpness > sharpest_value
                sharpest_image = temp;
                sharpest_value = sharpness;
            end
        end
        
        img_hr = sharpest_image;
        
        h = min(size(img_hr, 1), size(img_sr, 1));
        w = min(size(img_hr, 2), size(img_sr, 2));
        
        border = 6 + 2;
        
        img_hr_ycbcr = rgb2ycbcr(img_hr);
        img_hr_y = img_hr_ycbcr(1+border:h-border, 1+border:w-border, 1);
        img_sr_ycbcr = rgb2ycbcr(img_sr);
        img_sr_y = img_sr_ycbcr(1+border:h-border, 1+border:w-border, 1);

        rmse_video(idx_frame-8) = sqrt(mean((img_hr_y(:)-img_sr_y(:)).^2));
        psnr_video(idx_frame-8) = cal_psnr(img_sr_y, img_hr_y);
        ssim_video(idx_frame-8) = cal_ssim(img_sr_y, img_hr_y);
    end
    psnr_set(idx_video) = mean(psnr_video);
    ssim_set(idx_video) = mean(ssim_video);
    rmse_set(idx_video) = mean(rmse_video);
    disp([video_name{idx_video},'---Mean PSNR: ', num2str(mean(psnr_video),'%0.4f'),', Mean SSIM: ', num2str(mean(ssim_video),'%0.4f'),', Mean RMSE: ', num2str(mean(rmse_video),'%0.4f')])
end
disp(['---------------------------------------------'])
disp(['Set ',' SR---Mean PSNR: ', num2str(mean(psnr_set),'%0.4f'),', Mean SSIM: ', num2str(mean(ssim_set),'%0.4f'),', Mean RMSE: ', num2str(mean(rmse_set),'%0.4f')])


% Estimate sharpness using the gradient magnitude.
% sum of all gradient norms / number of pixels give us the sharpness
% metric.
function [sharpness]=estimate_sharpness(G)

[Gx, Gy]=gradient(G);

S=sqrt(Gx.*Gx+Gy.*Gy);
sharpness=sum(sum(S))./(numel(Gx));

end