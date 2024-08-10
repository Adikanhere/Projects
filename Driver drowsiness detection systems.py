import cv2
import dlib
from scipy.spatial import distance
import imutils
from imutils import face_utils

def eye_aspect_ratio(eye):

    # Distances of the vertical eye
    A = distance.euclidean(eye[1],eye[5])
    B= distance.euclidean(eye[2],eye[4])

    # Distances of the horizontal eye
    C =distance.euclidean(eye[0],eye[3])

    # eye aspect ratio
    ear = (A+B) / (2.0*C)
    return ear

# Thresholds and consecutive frames
EYE_AR_THRESH=0.3
EYE_AR_CONSEC_FRAMES=48

# frame counter and the total no of blinks
COUNTER = 0
ALARM_ON = False

# Initialize dlib's face detector
print("[INFO] Loading facial landmark predictor...")
detector =dlib.get_frontal_face_detector()
predictor =dlib.shape_predictor('shape_predictor_68_face_landmarks.dat')

# facial landmarks of the left and right eye
(lStart, lEnd)= face_utils.FACIAL_LANDMARKS_IDXS["left_eye"]
(rStart, rEnd) =face_utils.FACIAL_LANDMARKS_IDXS["right_eye"]

# video stream and camera sensor
print("[INFO] Starting video stream...")
vs = cv2.VideoCapture(0)
cv2.namedWindow("Frame")

while True:
    # video stream, resize it and convert to grayscale
    ret, frame =vs.read()
    if not ret:
        break
    frame =imutils.resize(frame, width=450)
    gray= cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)

    # Detect faces in the grayscale frame
    rects = detector(gray, 0)

    # Loop over the face detections
    for rect in rects:
        # convert the landmark (x, y) coordinates to a NumPy array
        shape = predictor(gray, rect)
        shape=face_utils.shape_to_np(shape)

        # Extract the left and right eye coordinates, compute the eye aspect ratio for both eyes
        leftEye= shape[lStart:lEnd]
        rightEye =shape[rStart:rEnd]
        leftEAR =eye_aspect_ratio(leftEye)
        rightEAR=eye_aspect_ratio(rightEye)

        # Average the eyes ratio
        ear = (leftEAR + rightEAR) / 2.0

        # visualize each of the eyes
        leftEyeHull = cv2.convexHull(leftEye)
        rightEyeHull = cv2.convexHull(rightEye)
        cv2.drawContours(frame, [leftEyeHull], -1,(0, 255, 0), 1)
        cv2.drawContours(frame, [rightEyeHull], -1,(0, 255, 0), 1)

        # Check eye aspect ratio
        if ear<EYE_AR_THRESH:
            COUNTER +=1

            # eyes were closed then sound the alarm
            if COUNTER >= EYE_AR_CONSEC_FRAMES:
                if not ALARM_ON:
                    ALARM_ON=True

                # alarm on the frame
                cv2.putText(frame, "DROWSINESS DETECTED!", (10, 30),
                            cv2.FONT_HERSHEY_SIMPLEX,0.7, (0,0,255), 2)

        # blink threshold, so reset the counter and alarm
        else:
            COUNTER=0
            ALARM_ON=False

        # eye ratio on the frame
        cv2.putText(frame, "EAR: {:.2f}".format(ear), (300, 30),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)

    # Show the frame
    cv2.imshow("Frame",frame)

    # `q` key was pressed, break the loop
    if cv2.waitKey(1) & 0xFF ==ord("q"):
        break

# Clean up
cv2.destroyAllWindows()
vs.release()
