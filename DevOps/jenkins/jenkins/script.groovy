 def greet(name) {
    echo "Hello, ${name}!"
}

def deployApp(){
    echo "deploying version ${params.VERSION}"
}

return this