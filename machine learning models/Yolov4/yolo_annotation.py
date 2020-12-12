# Python code for mapping annotations.json file to yolo format
# NOTE- All Images should be in 'Data/' folder

###### JSON INFO. #############
# External ID- image file name
# bbox- bounding boxes


import json 
f = open('annotations.json') 
data = json.load(f)
f.close() 




# function that turns XMin, YMin, XMax, YMax coordinates to normalized yolo format
def convert(filename_str, coords):
    image = cv2.imread(filename_str)
    coords[2] -= coords[0]
    coords[3] -= coords[1]
    x_diff = int(coords[2]/2)
    y_diff = int(coords[3]/2)
    coords[0] = coords[0]+x_diff
    coords[1] = coords[1]+y_diff
    coords[0] /= int(image.shape[1])
    coords[1] /= int(image.shape[0])
    coords[2] /= int(image.shape[1])
    coords[3] /= int(image.shape[0])
    return coords



def main()
    for img_info in tqdm(data):
      try:
        annotations = []
        for args in img_info['Label']['objects']:
          #(0, x, y, w, h)
          arr= np.array([args['bbox']['left'], args['bbox']['top'], args['bbox']['left']+args['bbox']['width'], args['bbox']['top']+args['bbox']['height']])
          arr= np.float32(arr)
          labels= convert(os.path.join('Data', img_info['External ID']), arr)
          line= str(0) + " " + str(labels[0]) + " " + str(labels[1]) + " " + str(labels[2]) + " " + str(labels[3])
          annotations.append(line)
      except:
        print('--------> REMOVE',img_info['External ID'])

      with open(os.path.join('Data', str(img_info['External ID'].split('.')[0]+'.txt')), "w") as outfile:
        for line in annotations:
          outfile.write(line)
          outfile.write("\n")
        outfile.close()




if __name__ == "__main__":
    main()





