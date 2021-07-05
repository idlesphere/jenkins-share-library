package com.libs.exe

def execute(def args) {
	log.info("Upload artifacts to S3 bucket")
	if ( !args.file || !args.bucket.name || !args.bucket.path ){
		error "Please provide needed information for S3 Upload"
	}
    try {
        s3Upload(file:args.file, bucket:args.bucket.name, path:args.bucket.path)
    } catch(e) {
        error e
    }
}