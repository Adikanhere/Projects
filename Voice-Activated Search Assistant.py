import pyttsx3
import datetime
import wikipedia
import webbrowser
import threading

# Initialize the pyttsx3 engine
engine = pyttsx3.init('sapi5')
voices = engine.getProperty('voices')
engine.setProperty('voice', voices[0].id)

# Function to make the engine speak
def speak(audio):
    engine.say(audio)
    engine.runAndWait()

# Function to greet the user based on the current time
def wishMe():
    hour = int(datetime.datetime.now().hour)
    if 0 <= hour < 12:
        speak("Good Morning Sir!")
    elif 12 <= hour < 18:
        speak("Good Afternoon Sir!")
    else:
        speak("Good Evening Sir!")
    speak("How may I help you?")

# Function to take command from the user through the console
def takeCommand():
    query = input("Type your command: ").lower()
    return query

# Function to search Wikipedia and speak the results
def searchWikipedia(query):
    try:
        results = wikipedia.summary(query, sentences=2)
        speak("According to Wikipedia")
        print(results)
        speak(results)
    except wikipedia.exceptions.DisambiguationError as e:
        speak("There are multiple results for your query. Please be more specific.")
        print(e.options)
    except wikipedia.exceptions.PageError:
        speak("No Wikipedia page found for your query.")

# Function to open Google with the search query
def openGoogle(query):
    webbrowser.open(f"https://www.google.com/search?q={query}")

# Main program
if __name__ == "__main__":
    wishMe()  # Call the function to greet the user
    while True:
        query = takeCommand().lower()

        if 'search about' in query:
            search_query = query.replace('search about', '').strip()
            if search_query:
                speak('Searching in Wikipedia...')
                
                # Start a thread to open Google
                google_thread = threading.Thread(target=openGoogle, args=(search_query,))
                google_thread.start()
                
                # Search Wikipedia and speak the results
                searchWikipedia(search_query)
            else:
                speak("Please specify what you want to search about.")

        elif 'the time' in query:
            strTime = datetime.datetime.now().strftime("%H:%M:%S")
            speak(f"Sir, the time is {strTime}")

        elif 'open google' in query:
            speak("Opening Google")
            webbrowser.open("https://www.google.com")

        else:
            speak("Sir, incorrect query. Please give the correct query.")
