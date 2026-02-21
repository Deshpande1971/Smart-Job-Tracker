import os
import subprocess
import sys

def check_file(path):
    if os.path.exists(path):
        print(f"[OK] Found: {path}")
        return True
    else:
        print(f"[ERROR] Missing: {path}")
        return False

def verify_phase1():
    print("Verifying Phase 1 implementation...")
    
    base_path = "src/main/java/com/jobtracker"
    required_dirs = [
        "config", "controller", "service/impl", "repository", 
        "entity", "dto", "security", "exception", "util"
    ]
    
    success = True
    
    # Check directories
    for d in required_dirs:
        if not check_file(os.path.join(base_path, d)):
            success = False
            
    # Check key files
    key_files = [
        os.path.join(base_path, "JobTrackerApplication.java"),
        os.path.join(base_path, "service/PlaceholderService.java"),
        os.path.join(base_path, "exception/GlobalExceptionHandler.java"),
        os.path.join(base_path, "exception/ResourceNotFoundException.java"),
        os.path.join(base_path, "dto/ErrorResponseDTO.java"),
        "src/main/resources/application.properties"
    ]
    
    for f in key_files:
        if not check_file(f):
            success = False

    if not success:
        print("\n[FAILED] Phase 1 structure is incomplete.")
        sys.exit(1)

    print("\n[OK] Phase 1 structure verify passed.")
    
    print("\nAttempting to compile the project (this might take a while)...")
    try:
        # Use shell=True for Windows and mvnw.cmd. Removed 'clean' to avoid file locks.
        result = subprocess.run(["mvnw.cmd", "compile"], shell=True, capture_output=True, text=True)
        if result.returncode == 0:
            print("[OK] Project compiled successfully.")
        else:
            print("[ERROR] Compilation failed.")
            print(result.stdout)
            print(result.stderr)
            sys.exit(1)
    except Exception as e:
        print(f"[ERROR] Could not run mvnw: {e}")
        sys.exit(1)

if __name__ == "__main__":
    verify_phase1()
